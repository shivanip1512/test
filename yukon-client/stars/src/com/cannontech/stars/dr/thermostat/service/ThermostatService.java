package com.cannontech.stars.dr.thermostat.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.hardware.model.SchedulableThermostatType;
import com.cannontech.stars.dr.thermostat.model.AccountThermostatSchedule;
import com.cannontech.stars.dr.thermostat.model.ThermostatManualEvent;
import com.cannontech.stars.dr.thermostat.model.ThermostatManualEventResult;
import com.cannontech.stars.dr.thermostat.model.ThermostatMode;
import com.cannontech.stars.dr.thermostat.model.ThermostatScheduleMode;
import com.cannontech.stars.dr.thermostat.model.ThermostatScheduleUpdateResult;
import com.cannontech.stars.dr.thermostat.model.TimeOfWeek;
import com.cannontech.user.YukonUserContext;

/**
 * Service class for thermostat operations
 */
public interface ThermostatService {

    /**
     * Method used to perform a manual event on a thermostat
     * @param account - Account for thermostat
     * @param event - Manual event to be performed
     * @param userContext - User context for manual event
     * @return Status of manual event execution
     */
    public ThermostatManualEventResult executeManualEvent(
            CustomerAccount account, ThermostatManualEvent event,
            YukonUserContext userContext);

    /**
     * Sends commands to thermostat for given thermostat schedule information.
     */
    public ThermostatScheduleUpdateResult sendSchedule(CustomerAccount account,
            AccountThermostatSchedule schedule, int thermostatId, TimeOfWeek timeOfWeek,
            ThermostatScheduleMode scheduleMode, YukonUserContext userContext);

    /**
     * Gets an AccountThermostatSchedule based off the energy company default schedule for the given type.
     * The AccountThermostatSchedule is set to -1 (so it is ready to be inserted when it is saved). Do not set this.
     * The AccountThermostatSchedule accountId and name are also blank and should be set before saving.
     */
    public AccountThermostatSchedule getAccountThermostatScheduleTemplate(int accountId, SchedulableThermostatType type);
    
    /**
     * Checks the AccountThermostatSchedule to see if it contains AccountThermostatScheduleEntries 
     * for all times of week associated with its schedule mode.  If entries are missing for any 
     * TimeOfWeek, entries are copied from TimeOfWeek.WEEKDAY and added to the schedule for that 
     * TimeOfWeek.
     */
    public void addMissingScheduleEntries(AccountThermostatSchedule schedule);
    
    /**
     * Prepares a ThermostatManualEvent for each thermostat being controlled, then attempts to
     * execute those events.
     */
    public ThermostatManualEventResult setupAndExecuteManualEvent(List<Integer> thermostatIds, 
                                                                   boolean hold, 
                                                                   boolean runProgram, 
                                                                   int tempInF, 
                                                                   String temperatureUnit, 
                                                                   String mode, 
                                                                   String fan, 
                                                                   CustomerAccount account, 
                                                                   YukonUserContext userContext);
    /**
     * Log a consumer's attempt to send manual thermostat settings.
     */
    public void logConsumerThermostatManualSaveAttempt(List<Integer> thermostatIds, 
                                                       YukonUserContext userContext, 
                                                       CustomerAccount account);
    /**
     * Log an operator's attempt to send manual thermostat settings.
     */
    public void logOperatorThermostatManualSaveAttempt(List<Integer> thermostatIds, 
                                                       YukonUserContext userContext, 
                                                       CustomerAccount account);
    
    /**
     * Stores the temperature unit that the customer last used, so next time we can display temps
     * in their preferred unit.
     */
    public void updateTempUnitForCustomer(String temperatureUnit, int customerId);
    
    /**
     * Retrieves the temperature from the request, or if that doesn't exist, retrieves the default
     * temperature for a manual event.  The resulting temperature is returned in Fahrenheit.
     */
    public int getTempOrDefaultInF(HttpServletRequest request, String temperatureUnit);
    
    /**
     * Parses a thermostat mode string value into a ThermostatMode object.  A blank string
     * will be translated to OFF.
     */
    public ThermostatMode getThermostatModeFromString(String mode);
}
