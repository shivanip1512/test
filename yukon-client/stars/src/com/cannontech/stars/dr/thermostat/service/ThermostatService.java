package com.cannontech.stars.dr.thermostat.service;

import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.hardware.model.Thermostat;
import com.cannontech.stars.dr.thermostat.model.ThermostatManualEvent;
import com.cannontech.stars.dr.thermostat.model.ThermostatManualEventResult;
import com.cannontech.stars.dr.thermostat.model.ThermostatMode;
import com.cannontech.stars.dr.thermostat.model.ThermostatSchedule;
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
     * Method used to save updates to a thermostat schedule
     * @param account - Account schedule is on
     * @param schedule - Schedule to be updated
     * @param mode - Thermostat mode in the schedule to be updated
     * @param timeOfWeek - Time of week in the schedule to be updated
     * @param applyToAll - True if schedule should be applied to all time of
     *            weeks
     * @param userContext - User context for user updating schedule
     */
    public void updateSchedule(CustomerAccount account,
            ThermostatSchedule schedule, ThermostatMode mode,
            TimeOfWeek timeOfWeek, boolean applyToAll,
            YukonUserContext userContext);

    /**
     * Method used to send a schedule to a thermostat
     * @param account - Account thermostat is on
     * @param schedule - Schedule to be sent
     * @param mode - Thermostat mode in the schedule to be sent
     * @param timeOfWeek - Time of week in the schedule to be sent
     * @param applyToAll - True if schedule should be applied to all time of
     *            weeks
     * @param userContext - User context for user sending schedule
     */
    public ThermostatScheduleUpdateResult sendSchedule(CustomerAccount account,
            ThermostatSchedule schedule, ThermostatMode mode,
            TimeOfWeek timeOfWeek, boolean applyToAll,
            YukonUserContext userContext);

    /**
     * Method to get the current schedule for a given thermostat
     * @param thermostat - Thermostat in question
     * @param accountId - Account id for thermostat
     * @return The current schedule or the energy company default schedule for
     *         the thermostat type if the thermostat doesn't have a schedule
     */
    public ThermostatSchedule getThermostatSchedule(Thermostat thermostat,
            CustomerAccount account);
}
