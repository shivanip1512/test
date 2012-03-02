package com.cannontech.stars.dr.thermostat.service.impl;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.joda.time.LocalTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.commands.impl.CommandCompletionException;
import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.model.YukonTextMessage;
import com.cannontech.common.temperature.Temperature;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.dr.hardware.model.HardwareSummary;
import com.cannontech.stars.dr.hardware.model.Thermostat;
import com.cannontech.stars.dr.hardware.service.CommandRequestHardwareExecutor;
import com.cannontech.stars.dr.thermostat.model.AccountThermostatSchedule;
import com.cannontech.stars.dr.thermostat.model.AccountThermostatScheduleEntry;
import com.cannontech.stars.dr.thermostat.model.ThermostatFanState;
import com.cannontech.stars.dr.thermostat.model.ThermostatManualEvent;
import com.cannontech.stars.dr.thermostat.model.ThermostatMode;
import com.cannontech.stars.dr.thermostat.model.ThermostatScheduleMode;
import com.cannontech.stars.dr.thermostat.model.ThermostatSchedulePeriod;
import com.cannontech.stars.dr.thermostat.model.ThermostatScheduleUpdateResult;
import com.cannontech.stars.dr.thermostat.model.TimeOfWeek;
import com.cannontech.stars.dr.thermostat.service.AbstractCommandExecutionService;
import com.google.common.collect.ListMultimap;

public class ExpressComCommandService extends AbstractCommandExecutionService {
    
    private static final Logger log = YukonLogManager.getLogger(ExpressComCommandService.class);
    @Autowired private InventoryDao inventoryDao;
    @Autowired private CommandRequestHardwareExecutor commandRequestHardwareExecutor;
    @Autowired private RolePropertyDao rolePropertyDao;

    @Override
    public void doManualAdjustment(ThermostatManualEvent event, Thermostat thermostat, LiteYukonUser user) 
    throws CommandCompletionException {
        
        String command = buildManualCommand(thermostat, event);
        commandRequestHardwareExecutor.execute(thermostat, command, user);
    }

    @Override
    public ThermostatScheduleUpdateResult doScheduleUpdate(CustomerAccount account, AccountThermostatSchedule ats,
                                 ThermostatScheduleMode mode, Thermostat stat, LiteYukonUser user) {
        
        ThermostatScheduleUpdateResult result = ThermostatScheduleUpdateResult.SEND_SCHEDULE_SUCCESS;
        for (TimeOfWeek timeOfWeek : mode.getAssociatedTimeOfWeeks()) {
            ThermostatScheduleUpdateResult tempResult = sendSchedulePart(account, ats, stat.getId(), timeOfWeek, mode, user);
            if (tempResult.isFailed()) {
                result = tempResult;  // do we really want to keep trying here? doesn't make a lot of sense
            }
        }
        
        return result;
    }
    
    @Transactional
    private ThermostatScheduleUpdateResult sendSchedulePart(CustomerAccount account, AccountThermostatSchedule ats, 
                                                       int thermostatId, TimeOfWeek timeOfWeek, ThermostatScheduleMode mode, LiteYukonUser user) {

        Thermostat stat = inventoryDao.getThermostatById(thermostatId);
        HardwareType type = stat.getType();

        if (type == HardwareType.UTILITY_PRO) {
            try {
                sendFixForUtilProScheduleMode(stat, mode, user);
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

        return ThermostatScheduleUpdateResult.SEND_SCHEDULE_SUCCESS;
    }
    
    /**
     * For Utility Pro's: update the mode if the 5-2 schedule role property is true
     */
    private void sendFixForUtilProScheduleMode(Thermostat stat, ThermostatScheduleMode mode, LiteYukonUser user) throws CommandCompletionException{
        // We have to update the schedule mode for Utility Pro thermostats every
        // time we update the schedule if 5-2 mode is enabled
        YukonRoleProperty modeProperty;
        modeProperty = YukonRoleProperty.ADMIN_ALLOW_THERMOSTAT_SCHEDULE_WEEKDAY_WEEKEND;
        
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
    private String buildScheduleCommand(Thermostat thermostat, AccountThermostatSchedule ats, TimeOfWeek timeOfWeek,
                        ThermostatScheduleMode mode) {

        
        ListMultimap<TimeOfWeek, AccountThermostatScheduleEntry> entriesByTimeOfWeekMap = ats.getEntriesByTimeOfWeekMultimap();
        List<AccountThermostatScheduleEntry> entries = entriesByTimeOfWeekMap.get(timeOfWeek);

        StringBuilder command = new StringBuilder();
        command.append("putconfig xcom schedule ");

        String timeOfWeekCommand = timeOfWeek.getCommandString();
        if (mode == ThermostatScheduleMode.ALL) {
            command.append("all ");
        } else {
            command.append(timeOfWeekCommand + " ");
        }
        
        int count = 1;
        for(ThermostatSchedulePeriod period : ats.getThermostatType().getPeriodStyle().getAllPeriods()){
            
            AccountThermostatScheduleEntry entry = entries.get(period.getEntryIndex());
            LocalTime startTime = entry.getStartTimeLocalTime();

            Temperature coolTemp = entry.getCoolTemp();
            Temperature heatTemp = entry.getHeatTemp();

            if (period.isPsuedo()) {
                // sending two time/temp values
                command.append("HH:MM,");
                command.append("ff,");
                command.append("ff");

            } else {
                
                String startTimeString = startTime.toString("HH:mm");
                command.append(startTimeString + ",");
                command.append(heatTemp.toFahrenheit().toIntValue() + ",");
                command.append(coolTemp.toFahrenheit().toIntValue());
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


        if (event.isRunProgram()) {
            // Run scheduled program
            command.append(" run");
        } else {
            ThermostatMode mode = event.getMode();
            if (mode != null) {
                command.append(" system ").append(mode.getCommandString());
            }
            
            // Set manual values
            Integer coolTemperatureInF = null;
            if (event.getPreviousCoolTemperature() != null) 
                coolTemperatureInF = event.getPreviousCoolTemperature().toFahrenheit().toIntValue();
            
            Integer heatTemperatureInF = null;
            if (event.getPreviousHeatTemperature() != null) 
                heatTemperatureInF = event.getPreviousHeatTemperature().toFahrenheit().toIntValue();
            
            // The command was sent from an autoModeEnabled page.  Send both temperatures.
            if(thermostat.getType().isAutoModeEnableable() && event.isAutoModeEnabledCommand()) {
                command.append(" heattemp ").append(heatTemperatureInF);
                command.append(" cooltemp ").append(coolTemperatureInF);

            } else if (thermostat.getType().isUtilityProType() && mode == ThermostatMode.HEAT) {
                command.append(" heattemp ").append(heatTemperatureInF);
            
            } else if (thermostat.getType().isUtilityProType() && mode == ThermostatMode.COOL) {
                command.append(" cooltemp ").append(coolTemperatureInF);

            } else {
                int temperatureInF = (mode == ThermostatMode.HEAT) ? heatTemperatureInF : coolTemperatureInF;
                command.append(" temp ").append(temperatureInF);
            }
            
            ThermostatFanState fanState = event.getFanState();
            if (fanState != null) {
                command.append(" fan ").append(fanState.getCommandString());
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


    public void sendTextMessage(YukonTextMessage message, Map<Integer, HardwareSummary> hardwareSummary){
        
        StringBuilder command = new StringBuilder();
        command.append("putconfig xcom data '");
        command.append(message.getMessage());
        command.append("' msgpriority 7");
        if(message.getDisplayDuration() != null){
            int displayDuration = message.getDisplayDuration().toPeriod().toStandardMinutes().getMinutes();
            command.append(" timeout ");
            command.append(displayDuration);
        }
        for (Integer inventoryId : message.getInventoryIds()) {
            try {
                StringBuilder serial = new StringBuilder();
                serial.append(" serial ");
                serial.append(hardwareSummary.get(inventoryId).getSerialNumber());
                commandRequestHardwareExecutor.execute(inventoryId, command.toString() + serial.toString(), message.getYukonUser());
            } catch (CommandCompletionException e) {
                //continue sending text messages
                log.warn("Unable to send text message to thermostat "+ inventoryId, e);
            }
        }
    }
    
}