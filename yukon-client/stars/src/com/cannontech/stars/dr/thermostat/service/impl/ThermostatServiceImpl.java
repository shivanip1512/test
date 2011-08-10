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
import com.cannontech.core.roleproperties.dao.EnergyCompanyRolePropertyDao;
import com.cannontech.database.data.activity.ActivityLogActions;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.core.service.YukonEnergyCompanyService;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.dr.hardware.model.CustomerAction;
import com.cannontech.stars.dr.hardware.model.CustomerEventType;
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
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * Implementation class for ThermostatService
 */
public class ThermostatServiceImpl implements ThermostatService {

    private Logger logger = YukonLogManager.getLogger(ThermostatServiceImpl.class);

    private AccountEventLogService accountEventLogService;
    private CustomerDao customerDao;
    private CustomerEventDao customerEventDao;
    private YukonEnergyCompanyService yukonEnergyCompanyService;
    private InventoryDao inventoryDao;
    private AccountThermostatScheduleDao accountThermostatScheduleDao;
    private ThermostatEventHistoryDao thermostatEventHistoryDao;
    private CommandServiceFactory commandServiceFactory;
    private EnergyCompanyRolePropertyDao energyCompanyRolePropertyDao;
    
    @Override
    @Transactional
    public ThermostatManualEventResult executeManual(CustomerAccount account, ThermostatManualEvent event, YukonUserContext context) {

        Integer thermostatId = event.getThermostatId();
        Thermostat thermostat = inventoryDao.getThermostatById(thermostatId);
        LiteYukonUser user = context.getYukonUser();

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
        accountEventLogService.thermostatManuallySet(user, account.getAccountNumber(), serialNumber);

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
    public void addMissingScheduleEntries(AccountThermostatSchedule ats) {
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
    public ThermostatManualEventResult validateTempAgainstLimits(List<Integer> thermostatIdsList, Temperature tempInF, ThermostatMode mode) {
        
        //The UI only allows selection of multiple thermostats of the same type, so the type of the
        //first thermostat should be the same as any others
        int firstThermostatId = thermostatIdsList.get(0);
        HardwareType type = inventoryDao.getThermostatById(firstThermostatId).getType();
        SchedulableThermostatType schedThermType = SchedulableThermostatType.getByHardwareType(type);
        Temperature minTempInF = schedThermType.getLowerLimit(mode.getHeatCoolSettingType()).toFahrenheit();
        Temperature maxTempInF = schedThermType.getUpperLimit(mode.getHeatCoolSettingType()).toFahrenheit();
        
        ThermostatManualEventResult message = null;
        if (tempInF.compareTo(maxTempInF) > 0) {
            message = ThermostatManualEventResult.MANUAL_INVALID_TEMP_HIGH;
        } else if (minTempInF.compareTo(tempInF) > 0) {
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
    
    @Override
    public ThermostatManualEventResult setupAndExecuteManualEvent(List<Integer> thermostatIds, 
                                                                  boolean hold, 
                                                                  boolean runProgram, 
                                                                  Temperature temperature, 
                                                                  String mode, 
                                                                  String fan, 
                                                                  CustomerAccount account, 
                                                                  YukonUserContext context) {
       ThermostatManualEventResult message = null;
       boolean failed = false;
       
       for (Integer thermostatId : thermostatIds) {
           // Build up manual event from submitted params
           ThermostatManualEvent event = new ThermostatManualEvent();
           event.setThermostatId(thermostatId);
           event.setHoldTemperature(hold);
           event.setPreviousTemperature(temperature);
           event.setRunProgram(runProgram);
           event.setEventType(CustomerEventType.THERMOSTAT_MANUAL);
           event.setAction(CustomerAction.MANUAL_OPTION);

           // Mode and fan can be blank
           if (runProgram) {
               event.setMode(ThermostatMode.DEFAULT);
           } else if (!StringUtils.isBlank(mode)) {
               ThermostatMode thermostatMode = ThermostatMode.valueOf(mode);
               event.setMode(thermostatMode);
           }
           if (!StringUtils.isBlank(fan)) {
               ThermostatFanState fanState = ThermostatFanState.valueOf(fan);
               event.setFanState(fanState);
           }

           // Execute manual event and get result
           message = executeManual(account, event, context);

           if (message.isFailed()) {
               failed = true;
           }
       }

       // If there was a failure and we are processing multiple thermostats,
       // set error to generic multiple error
       if (failed && thermostatIds.size() > 1) {
           message = ThermostatManualEventResult.MULTIPLE_ERROR;
       }
       
       return message;
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
            logMsg.append(", Temp:" + event.getPreviousTemperature().toString());
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
    
    // DI Setters
    @Autowired
    public void setAccountEventLogService(AccountEventLogService accountEventLogService) {
        this.accountEventLogService = accountEventLogService;
    }
    
    @Autowired
    public void setCustomerDao(CustomerDao customerDao) {
        this.customerDao = customerDao;
    }
    
    @Autowired
    public void setCustomerEventDao(CustomerEventDao customerEventDao) {
        this.customerEventDao = customerEventDao;
    }

    @Autowired
    public void setYukonEnergyCompanyService(YukonEnergyCompanyService yukonEnergyCompanyService) {
        this.yukonEnergyCompanyService = yukonEnergyCompanyService;
    }
    
    @Autowired
    public void setInventoryDao(InventoryDao inventoryDao) {
        this.inventoryDao = inventoryDao;
    }

    @Autowired
    public void setAccountThermostatScheduleDao(AccountThermostatScheduleDao accountThermostatScheduleDao) {
        this.accountThermostatScheduleDao = accountThermostatScheduleDao;
    }
    
    @Autowired
    public void setThermostatEventHistoryDao(ThermostatEventHistoryDao thermostatEventHistoryDao) {
        this.thermostatEventHistoryDao = thermostatEventHistoryDao;
    }
    
    @Autowired
    public void setCommandServiceFactory(CommandServiceFactory commandServiceFactory) {
        this.commandServiceFactory = commandServiceFactory;
    }

    @Autowired
    public void setEnergyCompanyRolePropertyDao(EnergyCompanyRolePropertyDao energyCompanyRolePropertyDao) {
        this.energyCompanyRolePropertyDao = energyCompanyRolePropertyDao;
    }
}