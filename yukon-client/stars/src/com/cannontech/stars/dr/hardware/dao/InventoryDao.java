package com.cannontech.stars.dr.hardware.dao;

import java.util.List;

import com.cannontech.stars.dr.hardware.model.Thermostat;

/**
 * Data Access interface for inventory
 */
public interface InventoryDao {

    /**
     * Method to get a list of thermostats based on an account id
     * @param accountId - Id of account to get stats for
     * @return List of thermostats for account
     */
    public List<Thermostat> getThermostatsByAccount(int accountId);
}
