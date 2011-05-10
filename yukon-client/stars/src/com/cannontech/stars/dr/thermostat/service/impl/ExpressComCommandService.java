package com.cannontech.stars.dr.thermostat.service.impl;

import java.util.Collection;

import org.apache.log4j.Logger;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.device.commands.impl.CommandCompletionException;
import com.cannontech.common.inventory.HardwareType;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.dr.hardware.model.Thermostat;
import com.cannontech.stars.dr.hardware.service.CommandRequestHardwareExecutor;
import com.cannontech.stars.dr.thermostat.model.AccountThermostatSchedule;
import com.cannontech.stars.dr.thermostat.model.AccountThermostatScheduleEntry;
import com.cannontech.stars.dr.thermostat.model.ThermostatFanState;
import com.cannontech.stars.dr.thermostat.model.ThermostatManualEvent;
import com.cannontech.stars.dr.thermostat.model.ThermostatMode;
import com.cannontech.stars.dr.thermostat.model.ThermostatScheduleMode;
import com.cannontech.stars.dr.thermostat.model.ThermostatScheduleUpdateResult;
import com.cannontech.stars.dr.thermostat.model.TimeOfWeek;
import com.cannontech.stars.dr.thermostat.service.AbstractCommandExecutionService;
import com.cannontech.stars.service.EnergyCompanyService;
import com.google.common.collect.Multimap;

public class ExpressComCommandService extends AbstractCommandExecutionService {
    
    private static final Logger log = YukonLogManager.getLogger(ExpressComCommandService.class);
    private InventoryDao inventoryDao;
    private CommandRequestHardwareExecutor commandRequestHardwareExecutor;
    private RolePropertyDao rolePropertyDao;
    private EnergyCompanyService energyCompanyService;

    @Override
    public void doManualAdjustment(ThermostatManualEvent event, Thermostat thermostat, LiteYukonUser user) 
    throws CommandCompletionException {
        
        String command = buildManualCommand(thermostat, event);
        commandRequestHardwareExecutor.execute(thermostat, command, user);
    }

    @Override
    public ThermostatScheduleUpdateResult doScheduleUpdate(CustomerAccount account, 
                                 AccountThermostatSchedule ats,
                                 ThermostatScheduleMode mode, 
                                 Thermostat stat, 
                                 LiteYukonUser user) {
        
        ThermostatScheduleUpdateResult result = ThermostatScheduleUpdateResult.UPDATE_SCHEDULE_SUCCESS;
        for (TimeOfWeek timeOfWeek : mode.getAssociatedTimeOfWeeks()) {
            ThermostatScheduleUpdateResult tempResult = sendSchedulePart(account, ats, stat.getId(), timeOfWeek, mode, user);
            if (tempResult.isFailed()) {
                result = tempResult;  // do we really want to keep trying here? doesn't make a lot of sense
            }
        }
        
        return result;
    }
    
    @Transactional
    private ThermostatScheduleUpdateResult sendSchedulePart(CustomerAccount account, 
                                                       AccountThermostatSchedule ats, 
                                                       int thermostatId, 
                                                       TimeOfWeek timeOfWeek,
                                                       ThermostatScheduleMode mode, 
                                                       LiteYukonUser user) {

        Thermostat stat = inventoryDao.getThermostatById(thermostatId);
        HardwareType type = stat.getType();

        if (type == HardwareType.UTILITY_PRO) {
            try {
                fixUtilProScheduleMode(stat, mode, user);
            } catch (CommandCompletionException e) {
                log.error("Failed to update thermostat schedule mode.", e);
                return ThermostatScheduleUpdateResult.UPDATE_SCHEDULE_ERROR;
            }
        }
        
        // Send command to thermostat
        try {
            String command = buildScheduleCommand(stat, ats, timeOfWeek, mode);
            commandRequestHardwareExecutor.execute(stat, command, user);
        } catch (CommandCompletionException e) {
            log.error("Failed to update thermostat schedule.", e);
            return ThermostatScheduleUpdateResult.UPDATE_SCHEDULE_ERROR;
        }

        saveAndLogUpdateEvent(account, ats, timeOfWeek, stat, user);

        return ThermostatScheduleUpdateResult.UPDATE_SCHEDULE_SUCCESS;
    }
    
    /**
     * For Utility Pro's: update the mode if the 5-2 schedule role property is true
     */
    private void fixUtilProScheduleMode(Thermostat stat, ThermostatScheduleMode mode, LiteYukonUser user) throws CommandCompletionException{
        boolean isOperator = energyCompanyService.isOperator(user);
        
        // We have to update the schedule mode for Utility Pro thermostats every
        // time we update the schedule if 5-2 mode is enabled
        YukonRoleProperty modeProperty;
        if (isOperator) {
            modeProperty = YukonRoleProperty.OPERATOR_THERMOSTAT_SCHEDULE_5_2;
        } else {
            modeProperty = YukonRoleProperty.RESIDENTIAL_THERMOSTAT_SCHEDULE_5_2;  // didn't we already decide to make this an EC enum?
        }
        
        boolean mode52Enabled = rolePropertyDao.checkProperty(modeProperty, user);

        if (mode52Enabled) {
            String serialString = " serial " + stat.getSerialNumber();
            if (mode == ThermostatScheduleMode.WEEKDAY_SAT_SUN 
                    || mode == ThermostatScheduleMode.ALL) {
                    
                // Schedule mode ALL is considered 5-1-1 by the stat
                commandRequestHardwareExecutor.execute(stat, "putconfig xcom raw 0x2b 0x08 0x03" + serialString, user);
                
            } else if (ThermostatScheduleMode.WEEKDAY_WEEKEND.equals(mode)) {
                commandRequestHardwareExecutor.execute(stat, "putconfig xcom raw 0x2b 0x08 0x02" + serialString, user);
            }
        }
    }
    
    /**
     * Helper method to build a command string for updating schedule (creates a
     * command for only the mode and time of week specified)
     * @param thermostat - Thermostat to update
     * @param ats - Full thermostat schedule
     * @param timeOfWeek - Time of week for schedule to update
     * @return Command string in format:
     *         <p>
     *         putconfig xcom schedule weekday 01:30, ff, 72, 08:00 ff, 72,
     *         15:00, ff, 72, 20:00, ff, 72 serial 1234567
     *         </p>
     */
    private String buildScheduleCommand(Thermostat thermostat,
                        AccountThermostatSchedule ats, 
                        TimeOfWeek timeOfWeek,
                        ThermostatScheduleMode mode) {

        
        Multimap<TimeOfWeek, AccountThermostatScheduleEntry> entriesByTimeOfWeekMap = ats.getEntriesByTimeOfWeekMultimap();
        Collection<AccountThermostatScheduleEntry> entries = entriesByTimeOfWeekMap.get(timeOfWeek);

        StringBuilder command = new StringBuilder();
        command.append("putconfig xcom schedule ");

        String timeOfWeekCommand = timeOfWeek.getCommandString();
        if (mode == ThermostatScheduleMode.ALL) {
            command.append("all ");
        } else {
            command.append(timeOfWeekCommand + " ");
        }

        int count = 1;
        for (AccountThermostatScheduleEntry entry : entries) {

            LocalTime startTime = entry.getStartTimeLocalTime();

            int coolTemp = entry.getCoolTemp();
            int heatTemp = entry.getHeatTemp();

            if (coolTemp == -1 && heatTemp == -1) {
                // temp of -1 means ignore this time/temp pair - used when only
                // sending two time/temp values
                command.append("HH:MM,");
                command.append("ff,");
                command.append("ff");

            } else {
                
                DateTimeFormatter timeFormatter = systemDateFormattingService.getCommandTimeFormatter();
                String startTimeString = timeFormatter.print(startTime);
                command.append(startTimeString + ",");
                command.append(heatTemp + ",");
                command.append(coolTemp);
            }

            // No trailing comma on the last season entry cool temp
            if (count++ != entries.size()) {
                command.append(", ");
            }

        }

        command.append(" serial " + thermostat.getSerialNumber());

        return command.toString();
    }
    
    /**
     * Helper method to build a command string for the manual event
     * @param thermostat - Thermostat to send command to
     * @param event - Event for the command
     * @return The command string
     */
    private String buildManualCommand(Thermostat thermostat, ThermostatManualEvent event) {

        StringBuilder command = new StringBuilder();
        command.append("putconfig xcom setstate ");

        ThermostatMode mode = event.getMode();
        if (mode != null) {
            String modeString = mode.getCommandString();
            command.append(" system ");
            command.append(modeString);
        }

        if (event.isRunProgram()) {
            // Run scheduled program
            command.append(" run");
        } else {
            // Set manual values
            Integer temperature = event.getPreviousTemperature();
            
            if (HardwareType.UTILITY_PRO.equals(thermostat.getType()) 
                    && mode.getDefinitionId() == YukonListEntryTypes.YUK_DEF_ID_THERM_MODE_HEAT) {
                
                command.append(" heattemp ");
                
            } else if (HardwareType.UTILITY_PRO.equals(thermostat.getType()) 
                    && mode.getDefinitionId() == YukonListEntryTypes.YUK_DEF_ID_THERM_MODE_COOL) {

                command.append(" cooltemp ");
            
            } else {
                command.append(" temp ");
            }
            
            command.append(temperature);

            ThermostatFanState fanState = event.getFanState();
            if (fanState != null) {
                String fanStatString = fanState.getCommandString();
                command.append(" fan ");
                command.append(fanStatString);
            }

            if (event.isHoldTemperature()) {
                command.append(" hold");
            }
        }

        String serialNumber = thermostat.getSerialNumber();
        command.append(" serial ");
        command.append(serialNumber);

        return command.toString();
    }
    
    @Autowired
    public void setInventoryDao(InventoryDao inventoryDao) {
        this.inventoryDao = inventoryDao;
    }
    
    @Autowired
    public void setCommandRequestHardwareExecutor(CommandRequestHardwareExecutor commandRequestHardwareExecutor) {
        this.commandRequestHardwareExecutor = commandRequestHardwareExecutor;
    }
    
    @Autowired
    public void setRolePropertyDao(RolePropertyDao rolePropertyDao) {
        this.rolePropertyDao = rolePropertyDao;
    }
    
    @Autowired
    public void setEnergyCompanyService(EnergyCompanyService energyCompanyService) {
        this.energyCompanyService = energyCompanyService;
    }
    
}