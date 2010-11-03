package com.cannontech.stars.dr.thermostat.service;

import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.hardware.model.SchedulableThermostatType;
import com.cannontech.stars.dr.thermostat.model.AccountThermostatSchedule;
import com.cannontech.stars.dr.thermostat.model.ThermostatManualEvent;
import com.cannontech.stars.dr.thermostat.model.ThermostatManualEventResult;
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
}
