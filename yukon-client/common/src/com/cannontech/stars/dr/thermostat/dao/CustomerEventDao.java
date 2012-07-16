package com.cannontech.stars.dr.thermostat.dao;

import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.hardware.model.Thermostat;
import com.cannontech.stars.dr.thermostat.model.AccountThermostatSchedule;
import com.cannontech.stars.dr.thermostat.model.CustomerThermostatEvent;
import com.cannontech.stars.dr.thermostat.model.ThermostatManualEvent;
import com.cannontech.stars.dr.thermostat.model.TimeOfWeek;

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

    /**
     * Save the ActivityLog and LMCustomerEventBase entries for a schedule schedule update.
     */
    public void saveAndLogScheduleUpdate(CustomerAccount account, AccountThermostatSchedule schedule,
                                  TimeOfWeek tow, Thermostat stat, LiteYukonUser user);
}
