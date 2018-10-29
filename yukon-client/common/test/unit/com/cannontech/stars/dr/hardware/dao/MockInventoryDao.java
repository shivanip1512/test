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
import com.cannontech.common.util.MethodNotImplementedException;
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
import com.google.common.collect.Maps;

public class MockInventoryDao implements InventoryDao {
    
    private Map<Integer, Integer> inventoryToDeviceMap = Maps.newHashMap();
    
    public void setInventoryToDeviceMap(Map<Integer, Integer> inventoryToDeviceMap) {
        this.inventoryToDeviceMap = inventoryToDeviceMap;
    }
    
    @Override
    public Map<Integer, Integer> getDeviceIds(Iterable<Integer> inventoryIds) {
        Map<Integer, Integer> filteredInventoryToDevices = Maps.newHashMap();
        for(Integer inventoryId : inventoryIds) {
            Integer deviceId = inventoryToDeviceMap.get(inventoryId);
            if(deviceId != null) {
                filteredInventoryToDevices.put(inventoryId, deviceId);
            }
        }
        return filteredInventoryToDevices;
    }
    
    /*
     * Unimplemented methods:
     */
    @Override
    public List<Thermostat> getThermostatsByAccount(CustomerAccount account) {
        throw new MethodNotImplementedException();
    }

    @Override
    public List<Thermostat> getThermostatsByAccountId(int accountId) {
        throw new MethodNotImplementedException();
    }

    @Override
    public List<HardwareSummary> getAllHardwareSummaryForAccount(int accountId) {
        throw new MethodNotImplementedException();
    }

    @Override
    public List<HardwareSummary> getAllHardwareSummaryForAccount(int accountId,
                                                                 Set<HardwareType> hardwareTypes) {
        throw new MethodNotImplementedException();
    }

    @Override
    public HardwareSummary findHardwareSummaryById(int inventoryId) {
        throw new MethodNotImplementedException();
    }

    @Override
    public Map<Integer, HardwareSummary> findHardwareSummariesById(Iterable<Integer> inventoryIds) {
        throw new MethodNotImplementedException();
    }

    @Override
    public List<HardwareSummary> getThermostatSummaryByAccount(CustomerAccount account) {
        throw new MethodNotImplementedException();
    }

    @Override
    public Thermostat getThermostatById(int thermostatId) {
        throw new MethodNotImplementedException();
    }

    @Override
    public List<String> getThermostatLabels(List<Integer> thermostatIds) {
        throw new MethodNotImplementedException();
    }

    @Override
    public void updateLabel(Thermostat thermostat, LiteYukonUser user) {
        throw new MethodNotImplementedException();
    }

    @Override
    public List<Integer> getInventoryIdsByAccount(int accountId) {
        throw new MethodNotImplementedException();
    }

    @Override
    public int getYukonDefinitionIdByEntryId(int entryId) {
        throw new MethodNotImplementedException();
    }

    @Override
    public InventoryIdentifier getYukonInventory(int inventoryId) {
        throw new MethodNotImplementedException();
    }

    @Override
    public InventoryIdentifier getYukonInventory(String serialNumber, int energyCompanyId) {
        throw new MethodNotImplementedException();
    }

    @Override
    public Set<InventoryIdentifier> getYukonInventory(Collection<Integer> inventoryIds) {
        throw new MethodNotImplementedException();
    }

    @Override
    public List<DisplayableLmHardware> getDisplayableLMHardware(List<? extends YukonInventory> inventoryIdentifiers) {
        throw new MethodNotImplementedException();
    }

    @Override
    public boolean checkAccountNumber(int inventoryId, String accountNumber) {
        throw new MethodNotImplementedException();
    }

    @Override
    public boolean checkdeviceType(int inventoryId, String deviceType) {
        throw new MethodNotImplementedException();
    }

    @Override
    public int getDeviceId(int inventoryId) {
        throw new MethodNotImplementedException();
    }

    @Override
    public int getAccountIdForInventory(int inventoryId) {
        throw new MethodNotImplementedException();
    }

    @Override
    public InventoryIdentifier getYukonInventoryForDeviceId(int deviceId) {
        throw new MethodNotImplementedException();
    }

    @Override
    public HardwareType getHardwareTypeById(int hardwareTypeId) {
        throw new MethodNotImplementedException();
    }

    @Override
    public List<DeviceAndPointValue> getZigbeeProblemDevices(String inWarehouseMsg) {
        throw new MethodNotImplementedException();
    }

    @Override
    public SearchResults<InventorySearchResult> search(InventorySearch inventorySearch,
                                                      Collection<Integer> ecIds, int start,
                                                      int pageCount, boolean starsMeters) {
        throw new MethodNotImplementedException();
    }

    @Override
    public Map<String, Integer> getSerialNumberToInventoryIdMap(Collection<String> serialNumbers,
                                                                int energyCompanyId) {
        throw new MethodNotImplementedException();
    }

    @Override
    public int getCategoryIdForTypeId(int hardwareTypeId, YukonEnergyCompany ec) {
        throw new MethodNotImplementedException();
    }

    @Override
    public List<Integer> getInventoryIds(Collection<String> serialNumbers, int energyCompanyId) {
        throw new MethodNotImplementedException();
    }

    @Override
    public String getMeterNumberForDevice(int deviceId) {
        throw new MethodNotImplementedException();
    }

    @Override
    public LiteLmHardware getLiteLmHardwareByInventory(YukonInventory inventory) {
        throw new MethodNotImplementedException();
    }

    @Override
    public List<LiteLmHardware> getLiteLmHardwareByPaos(List<? extends YukonPao> paos) {
        throw new MethodNotImplementedException();
    }

    @Override
    public List<InventoryIdentifier> getYukonInventoryForDeviceIds(List<Integer> deviceIds) {
        throw new MethodNotImplementedException();
    }

    @Override
    public DisplayableLmHardware getDisplayableLMHardware(int inventoryId) {
        throw new MethodNotImplementedException();
    }

    @Override
    public List<Thermostat> getThermostatsBySerialNumbers(EnergyCompany ec, Set<String> serialNumbers) {
        throw new MethodNotImplementedException();
    }
}
