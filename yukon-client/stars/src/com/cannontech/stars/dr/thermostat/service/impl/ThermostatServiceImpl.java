package com.cannontech.stars.dr.thermostat.service.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.clientutils.ActivityLogger;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.device.commands.impl.CommandCompletionException;
import com.cannontech.common.events.loggers.AccountEventLogService;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.core.service.SystemDateFormattingService;
import com.cannontech.database.data.activity.ActivityLogActions;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.stars.core.dao.ECMappingDao;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.dr.hardware.model.CustomerAction;
import com.cannontech.stars.dr.hardware.model.CustomerEventType;
import com.cannontech.stars.dr.hardware.model.HardwareType;
import com.cannontech.stars.dr.hardware.model.SchedulableThermostatType;
import com.cannontech.stars.dr.hardware.model.Thermostat;
import com.cannontech.stars.dr.hardware.service.CommandRequestHardwareExecutor;
import com.cannontech.stars.dr.thermostat.dao.AccountThermostatScheduleDao;
import com.cannontech.stars.dr.thermostat.dao.CustomerEventDao;
import com.cannontech.stars.dr.thermostat.dao.ThermostatScheduleDao;
import com.cannontech.stars.dr.thermostat.model.AccountThermostatSchedule;
import com.cannontech.stars.dr.thermostat.model.AccountThermostatScheduleEntry;
import com.cannontech.stars.dr.thermostat.model.CustomerThermostatEventBase;
import com.cannontech.stars.dr.thermostat.model.ThermostatFanState;
import com.cannontech.stars.dr.thermostat.model.ThermostatManualEvent;
import com.cannontech.stars.dr.thermostat.model.ThermostatManualEventResult;
import com.cannontech.stars.dr.thermostat.model.ThermostatMode;
import com.cannontech.stars.dr.thermostat.model.ThermostatScheduleMode;
import com.cannontech.stars.dr.thermostat.model.ThermostatScheduleUpdateResult;
import com.cannontech.stars.dr.thermostat.model.ThermostatSeasonEntry;
import com.cannontech.stars.dr.thermostat.model.TimeOfWeek;
import com.cannontech.stars.dr.thermostat.service.ThermostatService;
import com.cannontech.stars.util.StarsUtils;
import com.cannontech.user.YukonUserContext;

/**
 * Implementation class for ThermostatService
 */
public class ThermostatServiceImpl implements ThermostatService {

    private Logger logger = YukonLogManager.getLogger(ThermostatServiceImpl.class);
    private AccountEventLogService accountEventLogService;
    
    private CustomerEventDao customerEventDao;
    private InventoryDao inventoryDao;
    private ECMappingDao ecMappingDao;
    private ThermostatScheduleDao thermostatScheduleDao;
    private CommandRequestHardwareExecutor commandRequestHardwareExecutor;
    private RolePropertyDao rolePropertyDao;
    private SystemDateFormattingService systemDateFormattingService;
    private AccountThermostatScheduleDao accountThermostatScheduleDao;

    @Autowired
    public void setAccountEventLogService(AccountEventLogService accountEventLogService) {
        this.accountEventLogService = accountEventLogService;
    }
    
    @Autowired
    public void setCustomerEventDao(CustomerEventDao customerEventDao) {
        this.customerEventDao = customerEventDao;
    }

    @Autowired
    public void setInventoryDao(InventoryDao inventoryDao) {
        this.inventoryDao = inventoryDao;
    }

    @Autowired
    public void setEcMappingDao(ECMappingDao ecMappingDao) {
        this.ecMappingDao = ecMappingDao;
    }

    @Autowired
    public void setThermostatScheduleDao(
            ThermostatScheduleDao thermostatScheduleDao) {
        this.thermostatScheduleDao = thermostatScheduleDao;
    }

    @Autowired
    public void setCommandRequestHardwareExecutor(
                    CommandRequestHardwareExecutor commandRequestHardwareExecutor) {
        this.commandRequestHardwareExecutor = commandRequestHardwareExecutor;
    }
    
    @Autowired
    public void setRolePropertyDao(RolePropertyDao rolePropertyDao) {
		this.rolePropertyDao = rolePropertyDao;
	}
    
    @Autowired
    public void setSystemDateFormattingService(
    		SystemDateFormattingService systemDateFormattingService) {
		this.systemDateFormattingService = systemDateFormattingService;
	}
    
    @Autowired
    public void setAccountThermostatScheduleDao(AccountThermostatScheduleDao accountThermostatScheduleDao) {
		this.accountThermostatScheduleDao = accountThermostatScheduleDao;
	}

    @Override
    @Transactional
    public ThermostatManualEventResult executeManualEvent(
            CustomerAccount account, ThermostatManualEvent event,
            YukonUserContext userContext) {

        Integer thermostatId = event.getThermostatId();
        Thermostat thermostat = inventoryDao.getThermostatById(thermostatId);
        LiteYukonUser yukonUser = userContext.getYukonUser();

        // Make sure the device is available
        if (!thermostat.isAvailable()) {

            ThermostatManualEventResult error;
            if (StarsUtils.isOperator(yukonUser)) {
                error = ThermostatManualEventResult.OPERATOR_UNAVAILABLE_ERROR;
            } else {
                error = ThermostatManualEventResult.CONSUMER_MANUAL_ERROR;
            }
            return error;

        }

        // Make sure the device has a serial number
        String serialNumber = thermostat.getSerialNumber();
        if (StringUtils.isBlank(serialNumber)) {

            ThermostatManualEventResult error;
            if (StarsUtils.isOperator(yukonUser)) {
                error = ThermostatManualEventResult.OPERATOR_NO_SERIAL_ERROR;
            } else {
                error = ThermostatManualEventResult.CONSUMER_NO_SERIAL_ERROR;
            }
            return error;
        }

        // Build command to send to thermostat
        String command = this.buildManualEventCommand(thermostat, event);

        // Send command to thermostat
        try {
                        commandRequestHardwareExecutor.execute(
                                        thermostat, 
                                        command, 
                                        yukonUser);
        } catch (CommandCompletionException e) {
            logger.error("Thermostat manual event failed.", e);
            return ThermostatManualEventResult.CONSUMER_MANUAL_ERROR;
        }

        // Save manual event settings
        if (!event.isRunProgram()){
            customerEventDao.save(event);    
        }

        // Log manual event into activity log
        accountEventLogService.thermostatManuallySet(yukonUser,
                                                     account.getAccountNumber(),
                                                     thermostat.getSerialNumber());

        
        this.logManualEventActivity(thermostat,
                                    event,
                                    yukonUser.getUserID(),
                                    account.getAccountId(),
                                    account.getCustomerId());

        return event.isRunProgram() ? ThermostatManualEventResult.CONSUMER_MANUAL_PROGRAM_SUCCESS
                : ThermostatManualEventResult.CONSUMER_MANUAL_SUCCESS;
        }

    @Override
    public AccountThermostatSchedule getAccountThermostatScheduleTemplate(int accountId, SchedulableThermostatType type) {
    	
    	AccountThermostatSchedule schedule = accountThermostatScheduleDao.getEnergyCompanyDefaultScheduleByAccountAndType(accountId, type);
		schedule.setAccountThermostatScheduleId(-1); // 0 is valid for legacy reasons so make it -1 to induce an insert instead of an update
		schedule.setAccountId(accountId);
		schedule.setScheduleName("");
    	
    	return schedule;
    }
    
    
    public AccountThermostatSchedule getThermostatSchedule(Thermostat thermostat, CustomerAccount account) {

        AccountThermostatSchedule schedule = accountThermostatScheduleDao.findByInventoryId(thermostat.getId());

        // use energy company default schedule
        if (schedule == null) {
        	
            HardwareType hardwareType = thermostat.getType();
            SchedulableThermostatType schedulableThermostatType = SchedulableThermostatType.getByHardwareType(hardwareType);
            schedule = accountThermostatScheduleDao.getEnergyCompanyDefaultScheduleByAccountAndType(account.getAccountId(), schedulableThermostatType);
        }

        return schedule;
    }

    @Override
    @Transactional
    public ThermostatScheduleUpdateResult sendSchedule(CustomerAccount account,
    		AccountThermostatSchedule schedule, int thermostatId, TimeOfWeek timeOfWeek,
            ThermostatScheduleMode scheduleMode, YukonUserContext userContext) {

        Thermostat thermostat = inventoryDao.getThermostatById(thermostatId);
        LiteYukonUser yukonUser = userContext.getYukonUser();

        // Make sure the device is available
        boolean isOperator = StarsUtils.isOperator(yukonUser);
		if (!thermostat.isAvailable()) {

            ThermostatScheduleUpdateResult error;
            if (isOperator) {
                error = ThermostatScheduleUpdateResult.OPERATOR_UNAVAILABLE_ERROR;
            } else {
                error = ThermostatScheduleUpdateResult.CONSUMER_UPDATE_SCHEDULE_ERROR;
            }
            return error;

        }

        // Make sure the device has a serial number
        String serialNumber = thermostat.getSerialNumber();
        if (StringUtils.isBlank(serialNumber)) {

            ThermostatScheduleUpdateResult error;
            if (isOperator) {
                error = ThermostatScheduleUpdateResult.OPERATOR_NO_SERIAL_ERROR;
            } else {
                error = ThermostatScheduleUpdateResult.CONSUMER_NO_SERIAL_ERROR;
            }
            return error;
        }

        HardwareType type = thermostat.getType();

        // We have to update the schedule mode for Utility Pro thermostats every
        // time we update the schedule if 5-2 mode is enabled
        YukonRoleProperty modeProperty;
        if(isOperator) {
			modeProperty = YukonRoleProperty.OPERATOR_THERMOSTAT_SCHEDULE_5_2;
        } else {
        	modeProperty = YukonRoleProperty.RESIDENTIAL_THERMOSTAT_SCHEDULE_5_2;
        }
        boolean mode52Enabled = rolePropertyDao.checkProperty(modeProperty, yukonUser);
        if(type == HardwareType.UTILITY_PRO && mode52Enabled) {
        	
            try {
                    
                String serialString = " serial " + thermostat.getSerialNumber();
                if(scheduleMode == ThermostatScheduleMode.WEEKDAY_SAT_SUN || scheduleMode == ThermostatScheduleMode.ALL) {
                        
                	// Schedule mode ALL is considered 5-1-1 by the stat
                    commandRequestHardwareExecutor.execute(thermostat, "putconfig xcom raw 0x2b 0x08 0x03" + serialString, yukonUser);
                    
                } else if(ThermostatScheduleMode.WEEKDAY_WEEKEND.equals(scheduleMode)) {
                	
                    commandRequestHardwareExecutor.execute(thermostat, "putconfig xcom raw 0x2b 0x08 0x02" + serialString, yukonUser);
                }
                
            } catch (CommandCompletionException e) {
            	
                logger.error("Failed to update thermostat schedule mode.", e);
                return ThermostatScheduleUpdateResult.CONSUMER_UPDATE_SCHEDULE_ERROR;
            }
        }
        
        // Build the command to send to the thermostat
        String updateScheduleCommand = buildUpdateScheduleCommand(thermostat,
                                                                  schedule,
                                                                  timeOfWeek,
                                                                  scheduleMode);

        // Send command to thermostat
        try {
                        commandRequestHardwareExecutor.execute(
                                        thermostat, updateScheduleCommand, yukonUser);
        } catch (CommandCompletionException e) {
            logger.error("Failed to update thermostat schedule.", e);
            return ThermostatScheduleUpdateResult.CONSUMER_UPDATE_SCHEDULE_ERROR;
        }

        // Log the schedule update in the activity log
        LiteStarsEnergyCompany energyCompany = ecMappingDao.getCustomerAccountEC(account);

        
        this.logUpdateScheduleActivity(thermostat,
                                       schedule,
                                       timeOfWeek,
                                       yukonUser.getUserID(),
                                       account.getAccountId(),
                                       account.getCustomerId(),
                                       energyCompany.getEnergyCompanyID());

        // Save update schedule event
        CustomerThermostatEventBase event = new CustomerThermostatEventBase();
        event.setAction(CustomerAction.PROGRAMMING);
        event.setEventType(CustomerEventType.HARDWARE);
        event.setThermostatId(thermostatId);

        customerEventDao.save(event);

        return ThermostatScheduleUpdateResult.CONSUMER_UPDATE_SCHEDULE_SUCCESS;

    }

    /**
     * Helper method to build a command string for the manual event
     * @param thermostat - Thermostat to send command to
     * @param event - Event for the command
     * @return The command string
     */
    private String buildManualEventCommand(Thermostat thermostat,
            ThermostatManualEvent event) {

        StringBuilder commandString = new StringBuilder();

        if (thermostat.isTwoWay()) {
            commandString.append("putconfig epro setstate ");
        } else {
            commandString.append("putconfig xcom setstate ");
        }

        ThermostatMode mode = event.getMode();
        if (mode != null) {
            String modeString = mode.getCommandString();
            commandString.append(" system ");
            commandString.append(modeString);
        }

        if (event.isRunProgram()) {
            // Run scheduled program
            commandString.append(" run");
        } else {
            // Set manual values
            Integer temperature = event.getPreviousTemperature();
            if (HardwareType.UTILITY_PRO.equals(thermostat.getType()) && 
                mode.getDefinitionId() == YukonListEntryTypes.YUK_DEF_ID_THERM_MODE_HEAT){
                commandString.append(" heattemp ");
            } else if (HardwareType.UTILITY_PRO.equals(thermostat.getType()) && 
                mode.getDefinitionId() == YukonListEntryTypes.YUK_DEF_ID_THERM_MODE_COOL){
                commandString.append(" cooltemp ");
            } else {
                commandString.append(" temp ");
            }
            commandString.append(temperature);

            ThermostatFanState fanState = event.getFanState();
            if (fanState != null) {
                String fanStatString = fanState.getCommandString();
                commandString.append(" fan ");
                commandString.append(fanStatString);
            }

            if (event.isHoldTemperature()) {
                commandString.append(" hold");
            }
        }

        String serialNumber = thermostat.getSerialNumber();
        commandString.append(" serial ");
        commandString.append(serialNumber);

        return commandString.toString();
    }

    /**
     * Helper method to build a command string for updating schedule (creates a
     * command for only the mode and time of week specified)
     * @param thermostat - Thermostat to update
     * @param schedule - Full thermostat schedule
     * @param timeOfWeek - Time of week for schedule to update
     * @param applyToAll - True if schedule should be applied to all days
     * @return Command string in format:
     *         <p>
     *         putconfig xcom schedule weekday 01:30, ff, 72, 08:00 ff, 72,
     *         15:00, ff, 72, 20:00, ff, 72 serial 1234567
     *         </p>
     */
    private String buildUpdateScheduleCommand(Thermostat thermostat,
                        AccountThermostatSchedule schedule, TimeOfWeek timeOfWeek,
                        ThermostatScheduleMode scheduleMode) {

    	
    	Map<TimeOfWeek, List<AccountThermostatScheduleEntry>> entriesByTimeOfWeekMap = schedule.getEntriesByTimeOfWeekMap();
        List<AccountThermostatScheduleEntry> entries = entriesByTimeOfWeekMap.get(timeOfWeek);

        StringBuilder commandString = new StringBuilder();

        if (thermostat.isTwoWay()) {
            commandString.append("putconfig epro schedule ");
        } else {
            commandString.append("putconfig xcom schedule ");
        }

        String timeOfWeekCommand = timeOfWeek.getCommandString();
        if (scheduleMode == ThermostatScheduleMode.ALL) {
            commandString.append("all ");
        } else {
            commandString.append(timeOfWeekCommand + " ");
        }

        int count = 1;
        for (AccountThermostatScheduleEntry entry : entries) {

            LocalTime startTime = entry.getStartTimeLocalTime();

            int coolTemp = entry.getCoolTemp();
            int heatTemp = entry.getHeatTemp();

            if (coolTemp == -1 && heatTemp == -1) {
                // temp of -1 means ignore this time/temp pair - used when only
                // sending two time/temp values
                commandString.append("HH:MM,");
                commandString.append("ff,");
                commandString.append("ff");

            } else {
            	
            	DateTimeFormatter timeFormatter = systemDateFormattingService.getCommandTimeFormatter();
                String startTimeString = timeFormatter.print(startTime);
                commandString.append(startTimeString + ",");
                commandString.append(heatTemp + ",");
                commandString.append(coolTemp);
            }

            // No trailing comma on the last season entry cool temp
            if (count++ != entries.size()) {
                commandString.append(", ");
            }

        }

        commandString.append(" serial " + thermostat.getSerialNumber());

        return commandString.toString();
    }


    /**
     * Helper method to update time/temp values from a list of entries to
     * another
     * @param updatedEntries - Updated time/temp values
     * @param entries - Current time/temp values
     */
    private void updateScheduleEntries(
            List<ThermostatSeasonEntry> updatedEntries,
            List<ThermostatSeasonEntry> entries) {

        // Update the season entries
        for (int i = 0; i < updatedEntries.size(); i++) {

            ThermostatSeasonEntry entry = updatedEntries.get(i);
            ThermostatSeasonEntry originalEntry = entries.get(i);

            LocalTime startTime = entry.getStartTime();
            originalEntry.setStartTime(startTime);

            Integer coolTemperature = entry.getCoolTemperature();
            originalEntry.setCoolTemperature(coolTemperature);
            
            Integer heatTemperature = entry.getHeatTemperature();
            originalEntry.setHeatTemperature(heatTemperature);
        }
    }

    /**
     * Helper method to log the manual event to the activity log
     * @param thermostat - Thermostat for manual event
     * @param event - Event that happened
     * @param userId - Id of user submitting the event
     * @param accountId - Account id for the thermostat
     * @param customerId - Customer id for the thermostat
     */
    private void logManualEventActivity(Thermostat thermostat,
            ThermostatManualEvent event, int userId, int accountId,
            int customerId) {

        String tempUnit = event.getTemperatureUnit();

        StringBuilder logMsg = new StringBuilder("Serial #: " + thermostat.getSerialNumber());
        logMsg.append(", Mode:" + event.getMode());

        if (event.isRunProgram()) {
            logMsg.append(", Run Program");
        } else {
            logMsg.append(", Temp:" + event.getPreviousTemperatureForUnit() + tempUnit);
            if (event.isHoldTemperature()) {
                logMsg.append(" (HOLD)");
            }
            logMsg.append(", Fan: " + event.getFanState());
        }

        LiteStarsEnergyCompany energyCompany = ecMappingDao.getInventoryEC(thermostat.getId());
        Integer energyCompanyID = energyCompany.getEnergyCompanyID();

        ActivityLogger.logEvent(userId,
                                accountId,
                                energyCompanyID,
                                customerId,
                                ActivityLogActions.THERMOSTAT_MANUAL_ACTION,
                                logMsg.toString());
    }

    /**
     * Helper method to log an update schedule activity
     * @param thermostat - Thermostat that was updated
     * @param schedule - Schedule that was sent to thermostat
     * @param timeOfWeek - Time of week update applies to
     * @param userId - Id of user that did update
     * @param accountId - Account for updated thermostat
     * @param customerId - Customer for updated Thermostat
     * @param energyCompanyId - Energy company for updated thermostat
     */
    private void logUpdateScheduleActivity(Thermostat thermostat,
            AccountThermostatSchedule schedule, TimeOfWeek timeOfWeek, int userId, 
            int accountId, int customerId, int energyCompanyId) {

        String tempUnit = "F";

        StringBuilder logMessage = new StringBuilder();
        logMessage.append("Serial #:" + thermostat.getSerialNumber() + ", ");
        logMessage.append("Day:" + timeOfWeek.toString() + ", ");

        Map<TimeOfWeek, List<AccountThermostatScheduleEntry>> entriesByTimeOfWeekMap = schedule.getEntriesByTimeOfWeekMap();
        List<AccountThermostatScheduleEntry> entries = entriesByTimeOfWeekMap.get(timeOfWeek);
        DateTimeFormatter timeFormatter = systemDateFormattingService.getCommandTimeFormatter();

        if (HardwareType.COMMERCIAL_EXPRESSSTAT.equals(thermostat.getType())) {

        	AccountThermostatScheduleEntry occupiedEntry = entries.get(0);
            LocalTime occupiedStart = occupiedEntry.getStartTimeLocalTime();
            String occupiedDate = timeFormatter.print(occupiedStart);
            int coolOccupiedTemp = occupiedEntry.getCoolTemp();
            int heatOccupiedTemp = occupiedEntry.getHeatTemp();
            logMessage.append("Occupied:" + occupiedDate + "," + coolOccupiedTemp + tempUnit + 
                        "," + heatOccupiedTemp + tempUnit + ", ");

            AccountThermostatScheduleEntry unOccupiedEntry = entries.get(3);
            LocalTime unOccupiedStart = unOccupiedEntry.getStartTimeLocalTime();
            String unOccupiedDate = timeFormatter.print(unOccupiedStart);
            int coolUnOccupiedTemp = unOccupiedEntry.getCoolTemp();
            int heatUnOccupiedTemp = unOccupiedEntry.getHeatTemp();
            logMessage.append("Unoccupied:" + unOccupiedDate + "," + coolUnOccupiedTemp + tempUnit +
                        "," + heatUnOccupiedTemp + tempUnit);

        } else {

        	AccountThermostatScheduleEntry wakeEntry = entries.get(0);
            LocalTime wakeStart = wakeEntry.getStartTimeLocalTime();
            String wakeDate = timeFormatter.print(wakeStart);
            int wakeCoolTemp = wakeEntry.getCoolTemp();
            int wakeHeatTemp = wakeEntry.getHeatTemp();
            logMessage.append("Wake:" + wakeDate + "," + wakeCoolTemp + tempUnit + 
                        "," + wakeHeatTemp + tempUnit + ", ");

            AccountThermostatScheduleEntry leaveEntry = entries.get(1);
            LocalTime leaveStart = leaveEntry.getStartTimeLocalTime();
            String leaveDate = timeFormatter.print(leaveStart);
            int leaveCoolTemp = leaveEntry.getCoolTemp();
            int leaveHeatTemp = leaveEntry.getHeatTemp();
            logMessage.append("Leave:" + leaveDate + "," + leaveCoolTemp + tempUnit + 
                        "," + leaveHeatTemp + tempUnit + ", ");

            AccountThermostatScheduleEntry returnEntry = entries.get(2);
            LocalTime returnStart = returnEntry.getStartTimeLocalTime();
            String returnDate = timeFormatter.print(returnStart);
            int returnCoolTemp = returnEntry.getCoolTemp();
            int returnHeatTemp = returnEntry.getHeatTemp();
            logMessage.append("Return:" + returnDate + "," + returnCoolTemp + tempUnit + 
                        "," + returnHeatTemp + tempUnit + ", ");

            AccountThermostatScheduleEntry sleepEntry = entries.get(3);
            LocalTime sleepStart = sleepEntry.getStartTimeLocalTime();
            String sleepDate = timeFormatter.print(sleepStart);
            int sleepCoolTemp = sleepEntry.getCoolTemp();
            int sleepHeatTemp = sleepEntry.getHeatTemp();
            logMessage.append("Sleep:" + sleepDate + "," + sleepCoolTemp + tempUnit + 
                        "," + sleepHeatTemp + tempUnit + ", ");

        }

        ActivityLogger.logEvent(userId,
                                accountId,
                                energyCompanyId,
                                customerId,
                                ActivityLogActions.THERMOSTAT_SCHEDULE_ACTION,
                                logMessage.toString());
    }
}
