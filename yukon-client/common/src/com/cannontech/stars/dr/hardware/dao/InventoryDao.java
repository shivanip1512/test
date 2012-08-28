package com.cannontech.stars.dr.hardware.dao;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.inventory.InventoryIdentifier;
import com.cannontech.common.inventory.YukonInventory;
import com.cannontech.common.search.SearchResult;
import com.cannontech.common.util.Pair;
import com.cannontech.core.dynamic.impl.SimplePointValue;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.InventorySearchResult;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.displayable.model.DisplayableLmHardware;
import com.cannontech.stars.dr.hardware.model.HardwareSummary;
import com.cannontech.stars.dr.hardware.model.Thermostat;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.cannontech.stars.model.InventorySearch;
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
    
    /**
     * Get a List of specific types of hardware for an account 
     * @param accountId
     * @param hardwareTypes
     * @return
     */
    public List<HardwareSummary> getAllHardwareSummaryForAccount(int accountId, Set<HardwareType> hardwareTypes);
    
    public HardwareSummary findHardwareSummaryById(int inventoryId);

    /**
     * Get a map of inventoryId -> hardware summary for all of the passed in
     * inventory ids.
     */
    public Map<Integer, HardwareSummary> findHardwareSummariesById(Iterable<Integer> inventoryIds);
    
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
     * This method gets a list of all of the thermostat labels of the supplied thermostatIds.
     */
    public List<String> getThermostatLabels(List<Integer> thermostatIds);
    
    /**
     * Method to update the label of a thermostat
     * @param thermostat - Thermostat to save
     */
    public void updateLabel(Thermostat thermostat, LiteYukonUser user);

    public List<Integer> getInventoryIdsByAccount(int accountId);
    
    public int getYukonDefinitionIdByEntryId(int entryId);
    
    /**
     * Returns the InventoryIdentifier.  Includes all device types whether
     * they live in LmHardwareBase or MeterHardwareBase or as MCT's in YukonPAObject.
     */
    public InventoryIdentifier getYukonInventory(int inventoryId);

    public InventoryIdentifier getYukonInventory(String serialNumber, int energyCompanyId);
    
    /**
     * Returns a Set of InventoryIdentifier for all inventory ids.  Includes all device types whether
     * they live in LmHardwareBase or MeterHardwareBase or as MCT's in YukonPAObject.
     */
    public Set<InventoryIdentifier> getYukonInventory(Collection<Integer> inventoryIds);

    public List<DisplayableLmHardware> getDisplayableLMHardware(List<? extends YukonInventory> inventoryIdentifiers);

    public boolean checkAccountNumber(int inventoryId, String accountNumber);

    public boolean checkdeviceType(int inventoryId, String deviceType);

    public int getDeviceId(int inventoryId);

    /**
     * Returns the account id for this inventory or 0 if the inventory is not attached to an account.
     * @param inventoryId
     * @return accountId
     */
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

    /**
     * Returns search results for the search parameters provided.
     * @param inventorySearch The search parameters.
     * @param ecIds The energy company id's to filter on.
     * @param start The row count to start at.
     * @param pageCount The amount of rows per page.
     * @param starsMeters Wether this energy company uses 'STARS' meters (MeterHardwareBase) or MCT's
     * @return result The resulting list of inventory
     */
    public SearchResult<InventorySearchResult> search(InventorySearch inventorySearch, Collection<Integer> ecIds, int start, int pageCount, boolean starsMeters);

    /**
     * This method is a performance method.  This allows us to get a huge map of serial numbers to inventory ids, which can be
     * used to convert a bunch of serial numbers from the web service to their respective inventory Id.  This method will cut down on 
     * the amount of dao hits, but will force the user to use a map call to get the inventoryId for each serial number. 
     */
    public Map<String, Integer> getSerialNumberToInventoryIdMap(Collection<String> serialNumbers, int energyCompanyId);
    
    /**
     * Returns the category id based on hardware type id which either comes from 
     * LMHardwareBase:LMHardwareTypeID or from MeterHardwareBase:MeterTypeID.
     * If this energy company uses yukon for meters, they will only exist in InventoryBase
     * and they won't have a type id; their category defaults to 'MCT'. For stars meters the 
     * category will be 'NON_YUKON_METER'.
     */
    public int getCategoryIdForTypeId(int hardwareTypeId, YukonEnergyCompany ec);

    /**
     * This method allows us to pass in a list of serial numbers and get back a list of all of the inventory ids that exist in the system.
     */
    public List<Integer> getInventoryIds(Collection<String> serialNumbers, int energyCompanyId);

    /**
     * Retrieves the meter number from DeviceMeterGroup for an mct
     * @param deviceId
     * @return
     */
    public String getMeterNumberForDevice(int deviceId);

    public LiteLmHardware getLiteLmHardwareByInventoryId(int inventoryId);

    public LiteLmHardware getLiteLmHardwareByInventory(YukonInventory inventory);

    public List<LiteLmHardware> getLiteLmHardwareByInventory(List<InventoryIdentifier> inventory);
    
}