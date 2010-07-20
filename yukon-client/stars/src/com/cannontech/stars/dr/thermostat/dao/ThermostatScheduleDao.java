package com.cannontech.stars.dr.thermostat.dao;

import java.util.List;

/**
 * Data Access interface for thermostat schedules
 */
public interface ThermostatScheduleDao {


    /**
     * Method to delete events from LMThermostatManualEvent for an inventory id.
     * @param inventoryId
     */
    public void deleteManualEvents(Integer inventoryId);

    /**
     * Method to retrieve a list of manual event ids for an inventory id.
     * @param inventoryId
     * @return
     */
    public List<Integer> getAllManualEventIds(Integer inventoryId);

}
