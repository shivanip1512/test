package com.cannontech.stars.dr.thermostat.dao;

import com.cannontech.stars.dr.thermostat.model.CustomerThermostatEvent;
import com.cannontech.stars.dr.thermostat.model.ThermostatManualEvent;

/**
 * Data Access interface for customer thermostat events
 */
public interface CustomerEventDao {

    /**
     * Method to get the last manual event if ther is one
     * @param inventoryId - Id of thermostat to get event for
     * @return Latest event or empty event if there isn't a latest
     */
    public ThermostatManualEvent getLastManualEvent(int inventoryId);

    /**
     * Method to save a customer thermostat event
     * @param event - Event to save
     */
    public void save(CustomerThermostatEvent event);
}
