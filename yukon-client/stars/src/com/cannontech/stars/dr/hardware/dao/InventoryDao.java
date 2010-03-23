package com.cannontech.stars.dr.hardware.dao;

import java.util.List;

import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.hardware.model.HardwareSummary;
import com.cannontech.stars.dr.hardware.model.Thermostat;

/**
 * Data Access interface for inventory
 */
public interface InventoryDao {

    /**
     * Method to get a list of thermostats based on an account
     * @param account - Account to get stats for
     * @return List of thermostats for account
     */
    public List<Thermostat> getThermostatsByAccount(CustomerAccount account);

    /**
     * Method to get a list of thermostats based on an account id
     * @param accountId - Account Id to get stats for
     * @return List of thermostats for account
     */
    public List<Thermostat> getThermostatsByAccountId(int accountId);
    
    public List<HardwareSummary> getAllHardwareSummaryForAccount(int accountId);
    
    public HardwareSummary findHardwareSummaryById(int inventoryId);
    
    /**
     * Method to get a list of thermostat summary based on an account id
     * @param account - Account to get summary for
     * @return List of thermostat summary for account
     */
    public List<HardwareSummary> getThermostatSummaryByAccount(CustomerAccount account);
    
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

    /**
     * Method to get all inventory items for an account
     * @param accountId
     * @return
     */
    public List<Integer> getInventoryIdsByAccount(int accountId);
    
    public int getYukonDefinitionIdByEntryId(int entryId);
}
