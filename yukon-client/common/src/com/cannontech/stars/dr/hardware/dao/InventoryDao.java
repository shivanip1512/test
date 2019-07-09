package com.cannontech.stars.dr.hardware.dao;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.inventory.InventoryIdentifier;
import com.cannontech.common.inventory.YukonInventory;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.InventorySearchResult;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.displayable.model.DisplayableLmHardware;
import com.cannontech.stars.dr.hardware.model.DeviceAndPointValue;
import com.cannontech.stars.dr.hardware.model.HardwareSummary;
import com.cannontech.stars.dr.hardware.model.Thermostat;
import com.cannontech.stars.energyCompany.model.EnergyCompany;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.cannontech.stars.model.InventorySearch;
import com.cannontech.stars.model.LiteLmHardware;

/**
 * Data Access interface for inventory
 */
public interface InventoryDao {

    /**
     * Method to get a list of thermostats based on an account.
     * Note: this method will not return "non-configurable" thermostats (those that lack schedule & set-point support
     * in Yukon)
     * @param account - Account to get stats for
     * @return List of thermostats for account
     */
    List<Thermostat> getThermostatsByAccount(CustomerAccount account);

    /**
     * Method to get a list of thermostats based on an account id.
     * Note: this method will not return "non-configurable" thermostats (those that lack schedule & set-point support
     * in Yukon)
     * @param accountId - Account Id to get stats for
     * @return List of thermostats for account
     */
    List<Thermostat> getThermostatsByAccountId(int accountId);
    
    List<HardwareSummary> getAllHardwareSummaryForAccount(int accountId);
    

    /**
     * Retrieve all Yukon meters on an account. Unlike getAllHardwareSummaryForAccount, this retrieves 
     * meters that do not have an entry in LMHardwareBase. This does not retrieve STARS Inventory meters
     * @return HardwareSummary for all Yukon meters on an account
     */
    List<HardwareSummary> getMeterHardwareSummaryForAccount(int accountId);
    
    /**
     * Get a List of specific types of hardware for an account 
     * @param accountId
     * @param hardwareTypes
     * @return
     */
    List<HardwareSummary> getAllHardwareSummaryForAccount(int accountId, Set<HardwareType> hardwareTypes);
    
    HardwareSummary findHardwareSummaryById(int inventoryId);

    /**
     * Get a map of inventoryId -> hardware summary for all of the passed in
     * inventory ids.
     */
    Map<Integer, HardwareSummary> findHardwareSummariesById(Iterable<Integer> inventoryIds);
    
    /**
     * Method to get a list of thermostat summary based on an account id
     * @param account - Account to get summary for
     * @return List of thermostat summary for account
     */
    List<HardwareSummary> getThermostatSummaryByAccount(CustomerAccount account);
    
    /**
     * Method to get a thermostat by id
     * @param thermostatId - Id of thermostat to get
     * @return The thermostat
     */
    Thermostat getThermostatById(int thermostatId);

    /**
     * This method gets a list of all of the thermostat labels of the supplied thermostatIds.
     */
    List<String> getThermostatLabels(List<Integer> thermostatIds);
    
    /**
     * Method to update the label of a thermostat
     * @param thermostat - Thermostat to save
     */
    void updateLabel(Thermostat thermostat, LiteYukonUser user);

    List<Integer> getInventoryIdsByAccount(int accountId);
    
    int getYukonDefinitionIdByEntryId(int entryId);
    
    /**
     * Returns the InventoryIdentifier.  Includes all device types whether
     * they live in LmHardwareBase or MeterHardwareBase or as MCT's in YukonPAObject.
     */
    InventoryIdentifier getYukonInventory(int inventoryId);

    InventoryIdentifier getYukonInventory(String serialNumber, int energyCompanyId);
    
    /**
     * Returns a Set of InventoryIdentifier for all inventory ids.  Includes all device types whether
     * they live in LmHardwareBase or MeterHardwareBase or as MCT's in YukonPAObject.
     */
    Set<InventoryIdentifier> getYukonInventory(Collection<Integer> inventoryIds);

    List<DisplayableLmHardware> getDisplayableLMHardware(List<? extends YukonInventory> inventoryIdentifiers);

    boolean checkAccountNumber(int inventoryId, String accountNumber);

    boolean checkdeviceType(int inventoryId, String deviceType);

    int getDeviceId(int inventoryId);
     
    Map<Integer, Integer> getDeviceIds(Iterable<Integer> inventoryIds);

    /**
     * Returns the account id for this inventory or 0 if the inventory is not attached to an account.
     * @param inventoryId
     * @return accountId
     */
    int getAccountIdForInventory(int inventoryId);

    InventoryIdentifier getYukonInventoryForDeviceId(int deviceId);

    /**
     * Returns the HardwareType enum entry for the given type id.
     * If the energy company uses yukon for meters then type id 
     * will be zero.
     */
    HardwareType getHardwareTypeById(int hardwareTypeId);
    
    /**
     * Returns a list of zigbee devices that are attached to an account but
     * are not currently in the 'Connected' state, and thier point values.
     * @param MessageSourceAccessor
     */
    List<DeviceAndPointValue> getZigbeeProblemDevices(String inWarehouseMsg);

    /**
     * Returns search results for the search parameters provided.
     * @param inventorySearch The search parameters.
     * @param ecIds The energy company id's to filter on.
     * @param start The row count to start at.
     * @param pageCount The amount of rows per page.
     * @param starsMeters Wether this energy company uses 'STARS' meters (MeterHardwareBase) or MCT's
     * @return result The resulting list of inventory
     */
    SearchResults<InventorySearchResult> search(InventorySearch inventorySearch, Collection<Integer> ecIds, int start, int pageCount, boolean starsMeters);

    /**
     * This method is a performance method.  This allows us to get a huge map of serial numbers to inventory ids, which can be
     * used to convert a bunch of serial numbers from the web service to their respective inventory Id.  This method will cut down on 
     * the amount of dao hits, but will force the user to use a map call to get the inventoryId for each serial number. 
     */
    Map<String, Integer> getSerialNumberToInventoryIdMap(Collection<String> serialNumbers, int energyCompanyId);
    
    /**
     * Returns the category id based on hardware type id which either comes from 
     * LMHardwareBase:LMHardwareTypeID or from MeterHardwareBase:MeterTypeID.
     * If this energy company uses yukon for meters, they will only exist in InventoryBase
     * and they won't have a type id; their category defaults to 'MCT'. For stars meters the 
     * category will be 'NON_YUKON_METER'.
     */
    int getCategoryIdForTypeId(int hardwareTypeId, YukonEnergyCompany ec);

    /**
     * This method allows us to pass in a list of serial numbers and get back a list of all of the inventory ids that exist in the system.
     */
    List<Integer> getInventoryIds(Collection<String> serialNumbers, int energyCompanyId);

    /**
     * Retrieves the meter number from DeviceMeterGroup for an mct
     * @param deviceId
     * @return
     */
    String getMeterNumberForDevice(int deviceId);

    LiteLmHardware getLiteLmHardwareByInventory(YukonInventory inventory);

    List<LiteLmHardware> getLiteLmHardwareByPaos(List<? extends YukonPao> paos);

    List<InventoryIdentifier> getYukonInventoryForDeviceIds(List<Integer> deviceIds);

    DisplayableLmHardware getDisplayableLMHardware(int inventoryId);
    
    /**
     * Returns all thermostats for the Energy Company with the listed serial numbers.
     */
    List<Thermostat> getThermostatsBySerialNumbers(EnergyCompany ec, Set<String> serialNumbers);

    /**
     * Returns all Nest thermostats for the Energy Company that should be deleted.
     * 
     * @param accountNumbers - valid Nest account numbers to exclude
     */
    List<Thermostat> getNestThermostatsNotInListedAccounts(EnergyCompany ec, Set<String> accountNumbers);

    /**
     * Used by Account hardware list as filter to determine availability of meter picker
     * @return
     */
    boolean accountMeterWarehouseIsNotEmpty(Set<Integer> ecId, boolean includeMctsWithNoAccount);  
}