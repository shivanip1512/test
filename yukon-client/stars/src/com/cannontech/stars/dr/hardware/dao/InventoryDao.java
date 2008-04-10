package com.cannontech.stars.dr.hardware.dao;

import java.util.List;

import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.hardware.model.Thermostat;

/**
 * Data Access interface for inventory
 */
public interface InventoryDao {

    /**
     * Method to get a list of thermostats based on an account id
     * @param account - Account to get stats for
     * @return List of thermostats for account
     */
    public List<Thermostat> getThermostatsByAccount(CustomerAccount account);

    /**
     * Method to get a thermostat by id
     * @param thermostatId - Id of thermostat to get
     * @return The thermostat
     */
    public Thermostat getThermostatById(int thermostatId);
    
    /**
     * Method to save a thermostat
     * @param thermostat - Thermostat to save
     */
    public void save(Thermostat thermostat);
}
