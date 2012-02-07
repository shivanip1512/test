package com.cannontech.stars.dr.thermostat.service.impl;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.clientutils.ActivityLogger;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.commands.impl.CommandCompletionException;
import com.cannontech.common.events.loggers.AccountEventLogService;
import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.temperature.Temperature;
import com.cannontech.common.temperature.TemperatureUnit;
import com.cannontech.core.dao.CustomerDao;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.EnergyCompanyRolePropertyDao;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.activity.ActivityLogActions;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.core.service.YukonEnergyCompanyService;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.dr.hardware.model.CustomerAction;
import com.cannontech.stars.dr.hardware.model.CustomerEventType;
import com.cannontech.stars.dr.hardware.model.HeatCoolSettingType;
import com.cannontech.stars.dr.hardware.model.SchedulableThermostatType;
import com.cannontech.stars.dr.hardware.model.Thermostat;
import com.cannontech.stars.dr.thermostat.dao.AccountThermostatScheduleDao;
import com.cannontech.stars.dr.thermostat.dao.CustomerEventDao;
import com.cannontech.stars.dr.thermostat.dao.ThermostatEventHistoryDao;
import com.cannontech.stars.dr.thermostat.model.AccountThermostatSchedule;
import com.cannontech.stars.dr.thermostat.model.AccountThermostatScheduleEntry;
import com.cannontech.stars.dr.thermostat.model.ThermostatFanState;
import com.cannontech.stars.dr.thermostat.model.ThermostatManualEvent;
import com.cannontech.stars.dr.thermostat.model.ThermostatManualEventResult;
import com.cannontech.stars.dr.thermostat.model.ThermostatMode;
import com.cannontech.stars.dr.thermostat.model.ThermostatScheduleMode;
import com.cannontech.stars.dr.thermostat.model.ThermostatSchedulePeriod;
import com.cannontech.stars.dr.thermostat.model.ThermostatScheduleUpdateResult;
import com.cannontech.stars.dr.thermostat.model.TimeOfWeek;
import com.cannontech.stars.dr.thermostat.service.ThermostatCommandExecutionService;
import com.cannontech.stars.dr.thermostat.service.ThermostatService;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.cannontech.user.UserUtils;
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * Implementation class for ThermostatService
 */
public class ThermostatServiceImpl implements ThermostatService {

    private Logger logger = YukonLogManager.getLogger(ThermostatServiceImpl.class);

    @Autowired private AccountEventLogService accountEventLogService;
    @Autowired private AccountThermostatScheduleDao accountThermostatScheduleDao;
    @Autowired private CommandServiceFactory commandServiceFactory;
    @Autowired private CustomerAccountDao customerAccountDao;
    @Autowired private CustomerDao customerDao;
    @Autowired private CustomerEventDao customerEventDao;
    @Autowired private EnergyCompanyRolePropertyDao energyCompanyRolePropertyDao;
    @Autowired private InventoryDao inventoryDao;
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private ThermostatEventHistoryDao thermostatEventHistoryDao;
    @Autowired private YukonEnergyCompanyService yukonEnergyCompanyService;
    @Autowired private YukonUserDao yukonUserDao;

    @Override
    public ThermostatManualEventResult executeManualEvent(int thermostatId, Temperature heatTemp, Temperature coolTemp,
                                                          ThermostatMode thermostatMode, ThermostatFanState fanState, boolean hold, boolean autoModeEnabledCommand,
                                                          CustomerAccount account, LiteYukonUser user) {

        ThermostatManualEvent thermostatManualEvent = new ThermostatManualEvent();
        thermostatManualEvent.setThermostatId(thermostatId);
        thermostatManualEvent.setPreviousHeatTemperature(heatTemp);
        thermostatManualEvent.setPreviousCoolTemperature(coolTemp);
        thermostatManualEvent.setMode(thermostatMode);
        thermostatManualEvent.setFanState(fanState);
        thermostatManualEvent.setHoldTemperature(hold);
        thermostatManualEvent.setAutoModeEnabledCommand(autoModeEnabledCommand);
        thermostatManualEvent.setEventType(CustomerEventType.THERMOSTAT_MANUAL);
        thermostatManualEvent.setAction(CustomerAction.MANUAL_OPTION);
        
        // Execute manual event and get result
        return executeManual(account, thermostatManualEvent, user);

    }

    @Override
    public ThermostatManualEventResult runProgram(int thermostatId, LiteYukonUser user) {
        
        CustomerAccount customerAccount = customerAccountDao.getAccountByInventoryId(thermostatId);

        ThermostatManualEvent runProgramEvent = new ThermostatManualEvent();
        runProgramEvent.setThermostatId(thermostatId);
        runProgramEvent.setRunProgram(true);
        runProgramEvent.setMode(ThermostatMode.DEFAULT);
        runProgramEvent.setEventType(CustomerEventType.THERMOSTAT_MANUAL);
        runProgramEvent.setAction(CustomerAction.MANUAL_OPTION);
        
        return executeManual(customerAccount, runProgramEvent, user);
    }
    
    @Override
    @Transactional
    public ThermostatManualEventResult executeManual(CustomerAccount account, ThermostatManualEvent event, LiteYukonUser user) {

        Integer thermostatId = event.getThermostatId();
        Thermostat thermostat = inventoryDao.getThermostatById(thermostatId);

        // Make sure the device is available
        if (!thermostat.isAvailable()) {
            return ThermostatManualEventResult.UNAVAILABLE_ERROR;
        }

        // Make sure the device has a serial number
        String serialNumber = thermostat.getSerialNumber();
        if (StringUtils.isBlank(serialNumber)) {
            return ThermostatManualEventResult.NO_SERIAL_ERROR;
        }

        // Send command to thermostat
        try {
            sendManualAdjustmentCommand(event, thermostat, user);
        } catch (CommandCompletionException e) {
            logger.error("Thermostat manual event failed.", e);
            return ThermostatManualEventResult.MANUAL_ERROR;
        }

        // Save manual event settings
        if (!event.isRunProgram()) {
            customerEventDao.save(event);
        }

        // Log manual event into activity log
        accountEventLogService.thermostatManuallySet(user, serialNumber);

        logManualEventActivity(thermostat, event, user.getUserID(), account.getAccountId(), account.getCustomerId());
        
        //Log to thermostat history and return result
        if (event.isRunProgram()) {
            thermostatEventHistoryDao.logRestoreEvent(user, thermostatId);
            return ThermostatManualEventResult.MANUAL_PROGRAM_SUCCESS;
        } else {
            thermostatEventHistoryDao.logManualEvent(user, thermostatId, event);
            return ThermostatManualEventResult.MANUAL_SUCCESS;
        }
    }

    private void sendManualAdjustmentCommand(ThermostatManualEvent event, Thermostat thermostat, LiteYukonUser user) 
    throws CommandCompletionException {
        
        ThermostatCommandExecutionService service = commandServiceFactory.getCommandService(thermostat.getType());
        service.doManualAdjustment(event, thermostat, user);
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
    public ThermostatScheduleUpdateResult sendSchedule(CustomerAccount account,
                                                       AccountThermostatSchedule ats,
                                                       Iterable<Integer> thermostatIdsList,
                                                       ThermostatScheduleMode mode,
                                                       LiteYukonUser user) {
        
        ThermostatScheduleUpdateResult message = ThermostatScheduleUpdateResult.SEND_SCHEDULE_SUCCESS; // these enums suck, generalize (used to be SAVE_SCHEDULE_SUCCESS)
        int thermostatCount = 0;
        for (int thermostatId : thermostatIdsList) {
            thermostatCount++;
            message = sendScheduleToThermostat(account, ats, mode, thermostatId, user);

            if(!message.isFailed()) {
                //Log schedule send to thermostat history
                thermostatEventHistoryDao.logScheduleEvent(user, thermostatId, ats.getAccountThermostatScheduleId(), mode);
            }
        }

        if (message.isFailed() && thermostatCount > 1) {
            message = ThermostatScheduleUpdateResult.MULTIPLE_ERROR;
        }
        
        return message;
    }

    private ThermostatScheduleUpdateResult sendScheduleToThermostat(CustomerAccount account,
                                          AccountThermostatSchedule ats,
                                          ThermostatScheduleMode mode,
                                          int thermostatId,
                                          LiteYukonUser user) {
        
        Thermostat stat = inventoryDao.getThermostatById(thermostatId);
        HardwareType type = stat.getType();

        // Make sure the device is available
        if (!stat.isAvailable()) {
            return ThermostatScheduleUpdateResult.UNAVAILABLE_ERROR;
        }

        // Make sure the device has a serial number
        String serialNumber = stat.getSerialNumber();
        if (StringUtils.isBlank(serialNumber)) {
            return ThermostatScheduleUpdateResult.NO_SERIAL_ERROR;
        }
        
        ThermostatCommandExecutionService service = commandServiceFactory.getCommandService(type);
        return service.doScheduleUpdate(account, ats, mode, stat, user);
    }

    @Override
    public void addMissingScheduleEntries(AccountThermostatSchedule ats, AccountThermostatSchedule copyDefaultsFrom) {
        //iterate through each 'day' expected for this schedule based on its mode
        for (TimeOfWeek timeOfWeek : ats.getThermostatScheduleMode().getAssociatedTimeOfWeeks()) {
            //get the actual entries contained in this schedule
            List<AccountThermostatScheduleEntry> entries = ats.getEntriesByTimeOfWeekMultimap().get(timeOfWeek);

            if (entries.size() == 0) {
                //No entries for this this 'day' (TimeOfWeek).
                //Copy entries from WEEKDAY, which is used in most modes.
                List<AccountThermostatScheduleEntry> weekdayEntries = Lists.newArrayList();
                
                //Try to copy the entries from the default schedule
                if(copyDefaultsFrom != null){
                    
                    ThermostatScheduleMode defaultMode = copyDefaultsFrom.getThermostatScheduleMode();
                    
                    //by default we will grab the WEEKDAY entry as it is most common.
                    TimeOfWeek defaultTimeOfWeek = TimeOfWeek.WEEKDAY;
                    
                    /* The following switch and getEntriesByTimeOfWeekMultimap().get(defaultTimeOfWeek) call
                     *  will map values as such:
                     *  
                     *  default schedule        |       incomplete schedule
                     *  ALL                     |       ALL
                     *                          |           - WEEKDAY   = WEEKDAY
                     *                          |       SEVEN_DAY
                     *                          |           - (M-Su)     = WEEKDAY
                     *                          |       WEEKDAY_SAT_SUN
                     *                          |           - WEEKDAY   = WEEKDAY
                     *                          |           - SATURDAY  = WEEKDAY
                     *                          |           - SUNDAY    = WEEKDAY
                     *                          |       WEEKDAY_WEEKEND    
                     *                          |           - WEEKDAY   = WEEKDAY
                     *                          |           - WEEKEND   = WEEKDAY
                     *                          |
                     *  SEVEN_DAY               |       ALL
                     *                          |           - WEEKDAY   = MONDAY
                     *                          |       SEVEN_DAY
                     *                          |           - (M-Su)    = (M-Su)
                     *                          |       WEEKDAY_SAT_SUN
                     *                          |           - WEEKDAY   = MONDAY
                     *                          |           - SATURDAY  = SATURDAY
                     *                          |           - SUNDAY    = SUNDAY
                     *                          |       WEEKDAY_WEEKEND
                     *                          |           - WEEKDAY   = MONDAY
                     *                          |           - WEEKEND   = SATURDAY
                     *                          |
                     *  WEEKDAY_SAT_SUN         |       ALL
                     *                          |           - WEEKDAY   = WEEKDAY
                     *                          |       SEVEN_DAY
                     *                          |           - (M-F)     = WEEKDAY
                     *                          |           - SATURDAY  = SATURDAY
                     *                          |           - SUNDAY    = SUNDAY
                     *                          |       WEEKDAY_SAT_SUN
                     *                          |           - WEEKDAY   = WEEKDAY
                     *                          |           - SATURDAY  = SATURDAY
                     *                          |           - SUNDAY    = SUNDAY
                     *                          |       WEEKDAY_WEEKEND
                     *                          |           - WEEKDAY   = WEEKDAY
                     *                          |           - WEEKEND   = SATURDAY
                     *                          |
                     *  WEEKDAY_WEEKEND         |       ALL
                     *                          |           - WEEKDAY   = WEEKDAY
                     *                          |       SEVEN_DAY
                     *                          |           - (M-F)     = WEEKDAY
                     *                          |           - SATURDAY  = WEEKEND
                     *                          |           - SUNDAY    = WEEKEND
                     *                          |       WEEKDAY_SAT_SUN
                     *                          |           - WEEKDAY   = WEEKDAY
                     *                          |           - SATURDAY  = WEEKEND
                     *                          |           - SUNDAY    = WEEKEND
                     *                          |       WEEKDAY_WEEKEND
                     *                          |           - WEEKDAY   = WEEKDAY
                     *                          |           - WEEKEND   = WEEKEND
                     */

                    
                    switch(defaultMode){
                    case ALL:
                        //the default schedule should contain only weekday entries
                        break;
                        
                    case SEVEN_DAY:
                        if(timeOfWeek == TimeOfWeek.SATURDAY || timeOfWeek == TimeOfWeek.SUNDAY){
                            defaultTimeOfWeek = timeOfWeek;
                        }else{
                            //ALL mode does NOT have a WEEKDAY TimeOfWeek.
                            if(timeOfWeek == TimeOfWeek.WEEKDAY){
                                defaultTimeOfWeek = TimeOfWeek.MONDAY;
                            }else if(timeOfWeek == TimeOfWeek.WEEKEND){
                                defaultTimeOfWeek = TimeOfWeek.SATURDAY;
                            }else{
                                defaultTimeOfWeek = timeOfWeek;
                            }
                        }
                        break;
                        
                    case WEEKDAY_SAT_SUN:
                        if(timeOfWeek == TimeOfWeek.SATURDAY || timeOfWeek == TimeOfWeek.SUNDAY){
                            defaultTimeOfWeek = timeOfWeek;
                        }else if(timeOfWeek == TimeOfWeek.WEEKEND){
                            defaultTimeOfWeek = TimeOfWeek.SATURDAY;
                        }
                        break;
                        
                    case WEEKDAY_WEEKEND:
                        if(timeOfWeek == TimeOfWeek.SATURDAY || timeOfWeek == TimeOfWeek.SUNDAY){
                            defaultTimeOfWeek = TimeOfWeek.WEEKEND;
                        }
                        break;
                    }
                    
                    weekdayEntries = copyDefaultsFrom.getEntriesByTimeOfWeekMultimap().get(defaultTimeOfWeek);
                }else{
                    weekdayEntries = ats.getEntriesByTimeOfWeekMultimap().get(TimeOfWeek.WEEKDAY);
                }
                
                if(weekdayEntries.size() == 0){
                    Set<TimeOfWeek> keys = ats.getEntriesByTimeOfWeekMultimap().keySet();
                    if(keys.size() == 0){
                        //Nothing exists, create a default day of periods
                        List<ThermostatSchedulePeriod> periods = ats.getThermostatType().getPeriodStyle().getAllPeriods();
                        for(ThermostatSchedulePeriod period : periods){
                            AccountThermostatScheduleEntry atse = new AccountThermostatScheduleEntry();
                            atse.setTimeOfWeek(TimeOfWeek.WEEKDAY);
                            if(period.isPsuedo()){
                                atse.setCoolTemp(Temperature.fromFahrenheit(-1));
                                atse.setHeatTemp(Temperature.fromFahrenheit(-1));
                            }else{
                                atse.setCoolTemp(Temperature.fromFahrenheit(72));
                                atse.setHeatTemp(Temperature.fromFahrenheit(72));
                            }
                            atse.setStartTime(period.getDefaultStartTime());
                            
                            weekdayEntries.add(atse);
                        }
                    }else{
                        //copy the first one we can find as a last resort
                        for(TimeOfWeek day : keys){
                            weekdayEntries = ats.getEntriesByTimeOfWeekMultimap().get(day);
                            break;
                        }
                    }
                }
                
                List<AccountThermostatScheduleEntry> newEntries = Lists.newArrayList();
                for (AccountThermostatScheduleEntry weekdayEntry : weekdayEntries) {
                    AccountThermostatScheduleEntry copiedEntry = new AccountThermostatScheduleEntry();
                    copiedEntry.setTimeOfWeek(timeOfWeek);
                    copiedEntry.setCoolTemp(weekdayEntry.getCoolTemp());
                    copiedEntry.setHeatTemp(weekdayEntry.getHeatTemp());
                    copiedEntry.setStartTime(weekdayEntry.getStartTime());
                    newEntries.add(copiedEntry);
                }
                ats.addScheduleEntries(newEntries);
            }
        }
    }
    
    @Override
    public void addMissingScheduleEntriesForDefaultSchedules(AccountThermostatSchedule ats) {
        for (TimeOfWeek timeOfWeek : ats.getThermostatScheduleMode().getAssociatedTimeOfWeeks()) {
            List<AccountThermostatScheduleEntry> entries = ats.getEntriesByTimeOfWeekMultimap().get(timeOfWeek);
            if (entries.size() == 0) {
                //No entries for this time of week on this schedule.
                //Copy entries from WEEKDAY, which is used in most modes.
                List<AccountThermostatScheduleEntry> weekdayEntries = ats.getEntriesByTimeOfWeekMultimap().get(TimeOfWeek.WEEKDAY);
                if(weekdayEntries.size() == 0){
                    Set<TimeOfWeek> keys = ats.getEntriesByTimeOfWeekMultimap().keySet();
                    if(keys.size() == 0){
                        //Nothing exists, create a default day of periods
                        List<ThermostatSchedulePeriod> periods = ats.getThermostatType().getPeriodStyle().getAllPeriods();
                        for(ThermostatSchedulePeriod period : periods){
                            AccountThermostatScheduleEntry atse = new AccountThermostatScheduleEntry();
                            atse.setTimeOfWeek(TimeOfWeek.WEEKDAY);
                            if(period.isPsuedo()){
                                atse.setCoolTemp(Temperature.fromFahrenheit(-1));
                                atse.setHeatTemp(Temperature.fromFahrenheit(-1));
                            }else{
                                atse.setCoolTemp(Temperature.fromFahrenheit(72));
                                atse.setHeatTemp(Temperature.fromFahrenheit(72));
                            }
                            atse.setStartTime(period.getDefaultStartTime());
                            
                            weekdayEntries.add(atse);
                        }
                    }else{
                        //copy the first one we can find as a last resort
                        for(TimeOfWeek day : keys){
                            weekdayEntries = ats.getEntriesByTimeOfWeekMultimap().get(day);
                            break;
                        }
                    }
                }
                
                List<AccountThermostatScheduleEntry> newEntries = Lists.newArrayList();
                for (AccountThermostatScheduleEntry weekdayEntry : weekdayEntries) {
                    AccountThermostatScheduleEntry copiedEntry = new AccountThermostatScheduleEntry();
                    copiedEntry.setTimeOfWeek(timeOfWeek);
                    copiedEntry.setCoolTemp(weekdayEntry.getCoolTemp());
                    copiedEntry.setHeatTemp(weekdayEntry.getHeatTemp());
                    copiedEntry.setStartTime(weekdayEntry.getStartTime());
                    newEntries.add(copiedEntry);
                }
                ats.addScheduleEntries(newEntries);
            }
        }
    }
    
    @Override
    public ThermostatManualEventResult validateTempAgainstLimits(List<Integer> thermostatIdsList, Temperature heatTemperatureInF,
                                                                 Temperature coolTemperatureInF, ThermostatMode mode) {
        
        //The UI only allows selection of multiple thermostats of the same type, so the type of the
        //first thermostat should be the same as any others
        int firstThermostatId = thermostatIdsList.get(0);
        HardwareType type = inventoryDao.getThermostatById(firstThermostatId).getType();
        SchedulableThermostatType schedThermType = SchedulableThermostatType.getByHardwareType(type);
        
        ThermostatManualEventResult message = null;
        if (heatTemperatureInF.compareTo(schedThermType.getUpperLimit(HeatCoolSettingType.HEAT)) > 0 ||
              coolTemperatureInF.compareTo(schedThermType.getUpperLimit(HeatCoolSettingType.COOL))  > 0) {
            message = ThermostatManualEventResult.MANUAL_INVALID_TEMP_HIGH;
        } else if (schedThermType.getLowerLimit(HeatCoolSettingType.HEAT).compareTo(heatTemperatureInF) > 0 ||
                     schedThermType.getLowerLimit(HeatCoolSettingType.COOL).compareTo(coolTemperatureInF) > 0) {
            message = ThermostatManualEventResult.MANUAL_INVALID_TEMP_LOW;
        }
        
        return message;
    }
    
    @Override
    public Temperature getTempOrDefault(Double temperature, String temperatureUnit) {
        if(temperature == null) {
            return ThermostatManualEvent.DEFAULT_TEMPERATURE;
        } else {
            return Temperature.from(temperature, TemperatureUnit.fromAbbreviation(temperatureUnit));
        }
    }
    
    @Override
    public ThermostatMode getThermostatModeFromString(String mode) {
        ThermostatMode thermostatMode;
        if(StringUtils.isBlank(mode)) {
            thermostatMode = ThermostatMode.OFF;
        } else {
            thermostatMode = ThermostatMode.valueOf(mode);
        }
        return thermostatMode;
    }
    
    @Override
    public void updateTempUnitForCustomer(String temperatureUnit, int customerId) throws IllegalArgumentException {
        customerDao.setTemperatureUnit(customerId, temperatureUnit);
    }
    
    @Override
    public void logConsumerThermostatManualSaveAttempt(List<Integer> thermostatIds, YukonUserContext context, CustomerAccount account) {
        for (int thermostatId : thermostatIds) {
            Thermostat thermostat = inventoryDao.getThermostatById(thermostatId);
            
            accountEventLogService.thermostatManualSetAttemptedByConsumer(context.getYukonUser(),
                                                                          account.getAccountNumber(),
                                                                          thermostat.getSerialNumber());
        }
    }
    
    @Override
    public void logOperatorThermostatManualSaveAttempt(List<Integer> thermostatIds, YukonUserContext context, CustomerAccount account) {
        for (int thermostatId : thermostatIds) {
            Thermostat thermostat = inventoryDao.getThermostatById(thermostatId);
            
            accountEventLogService.thermostatManualSetAttemptedByOperator(context.getYukonUser(),
                                                                          account.getAccountNumber(),
                                                                          thermostat.getSerialNumber());
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
    private void logManualEventActivity(Thermostat thermostat, ThermostatManualEvent event, int userId, int accountId, int customerId) {

        StringBuilder logMsg = new StringBuilder("Serial #: " + thermostat.getSerialNumber());
        logMsg.append(", Mode:" + event.getMode());

        if (event.isRunProgram()) {
            logMsg.append(", Run Program");
        } else {
            logMsg.append(", CoolTemp:" + event.getPreviousCoolTemperature());
            logMsg.append(", HeatTemp:" + event.getPreviousHeatTemperature());
            if (event.isHoldTemperature()) {
                logMsg.append(" (HOLD)");
            }
            logMsg.append(", Fan: " + event.getFanState());
        }

        YukonEnergyCompany yukonEnergyCompany = yukonEnergyCompanyService.getEnergyCompanyByInventoryId(thermostat.getId());

        ActivityLogger.logEvent(userId, 
                                accountId, 
                                yukonEnergyCompany.getEnergyCompanyId(), 
                                customerId,
                                ActivityLogActions.THERMOSTAT_MANUAL_ACTION, 
                                logMsg.toString());
    }

    @Override
    public String getThermostatNameFromId(int thermostatId) {
        Thermostat thermo =  inventoryDao.getThermostatById(thermostatId);
        return thermo.getLabel();
    }
    
    @Override
    public Set<ThermostatScheduleMode> getAllowedThermostatScheduleModes(YukonEnergyCompany yukonEnergyCompany){
        Set<ThermostatScheduleMode> allowedModes = Sets.newHashSet();
        
        for(ThermostatScheduleMode mode : ThermostatScheduleMode.values()){
          //check to see if this mode is allowed by the energy company
            if(energyCompanyRolePropertyDao.getPropertyBooleanValue(mode.getAssociatedRoleProperty(), yukonEnergyCompany)){
                allowedModes.add(mode);
            }
        }
        return allowedModes;
    }
    
    @Override
    public Set<ThermostatScheduleMode> getAllowedThermostatScheduleModesByAccountId(int accountId) {
        YukonEnergyCompany yukonEnergyCompany = yukonEnergyCompanyService.getEnergyCompanyByAccountId(accountId);
        return getAllowedThermostatScheduleModes(yukonEnergyCompany);
    }
    
    @Override
    public boolean isAutoModeAvailable(int accountId, int thermostatId) {

        // Check to see if the thermostat allows it
        Thermostat thermostat = inventoryDao.getThermostatById(thermostatId);
        if (!thermostat.getType().isAutoModeEnableable()) {
            return false;
        }
        
        CustomerAccount customerAccount = customerAccountDao.getById(accountId);
        LiteContact contact = customerDao.getPrimaryContact(customerAccount.getCustomerId());
        
        // Get the consumer user to get their opt out limit.
        int userId = contact.getLoginID();
        LiteYukonUser user = yukonUserDao.getLiteYukonUser(userId);
        
        // If the account has a login and is a member of the Residential Customer Role, load its limits
        if(user.getUserID() != UserUtils.USER_DEFAULT_ID &&
           rolePropertyDao.checkRole(YukonRole.RESIDENTIAL_CUSTOMER, user)) {
            
            boolean autoThermostatModeEnabled = 
                    rolePropertyDao.getPropertyBooleanValue(YukonRoleProperty.RESIDENTIAL_AUTO_THERMOSTAT_MODE_ENABLED, user);
            
            return autoThermostatModeEnabled;
        }
        
        return false;
    }
}