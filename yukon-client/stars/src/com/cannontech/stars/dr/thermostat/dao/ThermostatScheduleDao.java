package com.cannontech.stars.dr.thermostat.dao;

import com.cannontech.stars.dr.hardware.model.HardwareType;
import com.cannontech.stars.dr.thermostat.model.ThermostatSchedule;

/**
 * Data Access interface for thermostat schedules
 */
public interface ThermostatScheduleDao {

    /**
     * Method to get the default thermostat schedule for an energy company based
     * on account id and thermostat type
     * @param accountId - Id of account to get energy company for
     * @param type - Type of thermostat to get default schedule for
     * @return The default schedule for the energy company
     */
    public ThermostatSchedule getEnergyCompanyDefaultSchedule(int accountId,
            HardwareType type);

    /**
     * Method to get the thermostat schedule for a piece of inventory
     * @param inventoryId - Id of inventory
     * @return The schedule for that inventory
     */
    public ThermostatSchedule getThermostatScheduleByInventoryId(int inventoryId);

    /**
     * Method to save a thermostat schedule
     * @param schedule - Schedule to save
     */
    public void save(ThermostatSchedule schedule);

}
