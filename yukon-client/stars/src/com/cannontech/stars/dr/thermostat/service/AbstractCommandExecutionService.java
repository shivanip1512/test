package com.cannontech.stars.dr.thermostat.service;

import java.util.List;

import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.ActivityLogger;
import com.cannontech.core.service.SystemDateFormattingService;
import com.cannontech.database.data.activity.ActivityLogActions;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.db.activity.ActivityLog;
import com.cannontech.stars.core.dao.ECMappingDao;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.hardware.model.CustomerAction;
import com.cannontech.stars.dr.hardware.model.CustomerEventType;
import com.cannontech.stars.dr.hardware.model.SchedulableThermostatType;
import com.cannontech.stars.dr.hardware.model.Thermostat;
import com.cannontech.stars.dr.thermostat.dao.CustomerEventDao;
import com.cannontech.stars.dr.thermostat.model.AccountThermostatSchedule;
import com.cannontech.stars.dr.thermostat.model.AccountThermostatScheduleEntry;
import com.cannontech.stars.dr.thermostat.model.CustomerThermostatEventBase;
import com.cannontech.stars.dr.thermostat.model.ThermostatSchedulePeriod;
import com.cannontech.stars.dr.thermostat.model.ThermostatSchedulePeriodStyle;
import com.cannontech.stars.dr.thermostat.model.TimeOfWeek;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.google.common.collect.ListMultimap;

public abstract class AbstractCommandExecutionService implements ThermostatCommandExecutionService {
    
    private ECMappingDao ecMappingDao;
    private CustomerEventDao customerEventDao;
    public SystemDateFormattingService systemDateFormattingService;

    @Override
    public void saveAndLogUpdateEvent(CustomerAccount account, 
                                       AccountThermostatSchedule schedule,
                                       TimeOfWeek timeOfWeek, 
                                       Thermostat stat, 
                                       LiteYukonUser user) {
        
        // Log the schedule update in the activity log
        LiteStarsEnergyCompany energyCompany = ecMappingDao.getCustomerAccountEC(account);
        logUpdateEvent(stat, schedule, timeOfWeek, user, account, energyCompany);

        // Save update schedule event
        CustomerThermostatEventBase event = new CustomerThermostatEventBase();
        event.setAction(CustomerAction.PROGRAMMING);
        event.setEventType(CustomerEventType.HARDWARE);
        event.setThermostatId(stat.getId());
        customerEventDao.save(event);
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
    private void logUpdateEvent(Thermostat thermostat, 
                                           AccountThermostatSchedule schedule, 
                                           TimeOfWeek timeOfWeek, 
                                           LiteYukonUser user, 
                                           CustomerAccount account,
                                           YukonEnergyCompany energyCompany) {

        // Log message
        StringBuilder logMessage = new StringBuilder();
        logMessage.append("Serial #:" + thermostat.getSerialNumber() + ", ");
        logMessage.append("Day:" + timeOfWeek.toString() + ", ");
        
        String tempUnit = "F";
        DateTimeFormatter timeFormatter = systemDateFormattingService.getCommandTimeFormatter();
        SchedulableThermostatType schedulableThermostatType = SchedulableThermostatType.getByHardwareType(thermostat.getType());
        ThermostatSchedulePeriodStyle periodStyle = schedulableThermostatType.getPeriodStyle();
        boolean useComma = false;

        ListMultimap<TimeOfWeek, AccountThermostatScheduleEntry> entriesByTimeOfWeekMap = schedule.getEntriesByTimeOfWeekMultimap();
        List<AccountThermostatScheduleEntry> entries = entriesByTimeOfWeekMap.get(timeOfWeek);
        
        for(ThermostatSchedulePeriod period : periodStyle.getRealPeriods()){
            //add a comma to separate entries if there are several
            if(useComma){ 
                logMessage.append(", ");
            } else {
                useComma = true;
            }
            AccountThermostatScheduleEntry atsEntry = entries.get(period.getEntryIndex());
            String entryDate = timeFormatter.print(atsEntry.getStartTimeLocalTime());
            int coolEntryTemp = atsEntry.getCoolTemp();
            int heatEntryTemp = atsEntry.getHeatTemp();
            logMessage.append(period + ": " + entryDate + "," + coolEntryTemp + tempUnit + "," + heatEntryTemp + tempUnit);
        }
        
        ActivityLog event = new ActivityLog();
        event.setUserID(user.getUserID());
        event.setAccountID(account.getAccountId());
        event.setEnergyCompanyID(energyCompany.getEnergyCompanyId());
        event.setCustomerID(account.getCustomerId());
        event.setAction(ActivityLogActions.THERMOSTAT_SCHEDULE_ACTION);
        event.setDescription(logMessage.toString());
        ActivityLogger.logEvent(event);
    }
    
    @Autowired
    public void setSystemDateFormattingService(SystemDateFormattingService systemDateFormattingService) {
        this.systemDateFormattingService = systemDateFormattingService;
    }
    
    @Autowired
    public void setEcMappingDao(ECMappingDao ecMappingDao) {
        this.ecMappingDao = ecMappingDao;
    }
    
    @Autowired
    public void setCustomerEventDao(CustomerEventDao customerEventDao) {
        this.customerEventDao = customerEventDao;
    }
    
}