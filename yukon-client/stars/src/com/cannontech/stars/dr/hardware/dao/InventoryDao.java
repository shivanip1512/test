package com.cannontech.stars.dr.hardware.dao;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.inventory.InventoryIdentifier;
import com.cannontech.common.inventory.YukonInventory;
import com.cannontech.common.util.Pair;
import com.cannontech.core.dynamic.impl.SimplePointValue;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.displayable.model.DisplayableLmHardware;
import com.cannontech.stars.dr.hardware.model.HardwareSummary;
import com.cannontech.stars.dr.hardware.model.Thermostat;
import com.cannontech.stars.model.LiteLmHardware;

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
     * Get a map of inventoryId -> hardware summary for all of the passed in
     * inventory ids.
     */
    public Map<Integer, HardwareSummary> findHardwareSummariesById(
            Iterable<Integer> inventoryIds);
    
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
     * Method to update the label of a thermostat
     * @param thermostat - Thermostat to save
     */
    public void updateLabel(Thermostat thermostat, LiteYukonUser user);

    public List<Integer> getInventoryIdsByAccount(int accountId);
    
    public int getYukonDefinitionIdByEntryId(int entryId);
    
    public InventoryIdentifier getYukonInventory(int inventoryId);

    public InventoryIdentifier getYukonInventory(String serialNumber, int energyCompanyId);
    
    public Set<InventoryIdentifier> getYukonInventory(Collection<Integer> inventoryIds);

    public List<DisplayableLmHardware> getDisplayableLMHardware(List<? extends YukonInventory> inventoryIdentifiers);

    public boolean checkAccountNumber(int inventoryId, String accountNumber);

    public boolean checkdeviceType(int inventoryId, String deviceType);

    public int getDeviceId(int inventoryId);

    public int getAccountIdForInventory(int inventoryId);

    public InventoryIdentifier getYukonInventoryForDeviceId(int deviceId);

    /**
     * Returns the HardwareType enum entry for the given type id.
     * If the energy company uses yukon for meters then type id 
     * will be zero.
     */
    public HardwareType getHardwareTypeById(int hardwareTypeId);
    
    /**
     * Returns the HardwareType enum entry for the give inventory id.
     */
    public HardwareType getHardTypeByInventoryId(int inventoryId);

    /**
     * Returns a list of zigbee devices that are attached to an account but
     * are not currently in the 'Connected' state, and thier point values.
     * @param MessageSourceAccessor
     * @return List<Pair<LiteLmHardware, SimplePointValue>>
     */
    public List<Pair<LiteLmHardware, SimplePointValue>> getZigbeeProblemDevices(String inWarehouseMsg);

}