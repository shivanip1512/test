package com.cannontech.stars.dr.hardware.dao;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.MethodNotImplementedException;
import com.cannontech.dr.assetavailability.DeviceRelayApplianceCategories;
import com.cannontech.dr.assetavailability.DeviceRelayApplianceCategory;
import com.cannontech.dr.assetavailability.InventoryRelayAppliance;
import com.cannontech.dr.assetavailability.InventoryRelayAppliances;
import com.cannontech.stars.database.data.lite.LiteAccountInfo;
import com.cannontech.stars.database.data.lite.LiteLmHardwareBase;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.dr.hardware.model.LMHardwareConfiguration;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

public class MockLmHardwareConfigurationDao implements LMHardwareConfigurationDao {
    private Multimap<Integer, Integer> loadGroupToInventoryMap;
    private Map<Integer, Integer> inventoryToDeviceMap;
    private Map<Integer, Integer> deviceToInventoryMap; //excludes one-way devices
    private Map<Integer, Map<Integer, Integer>> inventoryRelayApplianceMap;
    
    private PaoType paoType = PaoType.LCR6600_RFN;
    
    public MockLmHardwareConfigurationDao(Multimap<Integer, Integer> loadGroupToInventoryMap,
                                          Map<Integer, Integer> inventoryToDeviceMap,
                                          Map<Integer, Map<Integer, Integer>> inventoryRelayApplianceMap) {
        this.loadGroupToInventoryMap = loadGroupToInventoryMap;
        this.inventoryToDeviceMap = inventoryToDeviceMap;
        this.inventoryRelayApplianceMap = inventoryRelayApplianceMap;
        
        Map<Integer, Integer> deviceToInventoryMap = Maps.newHashMap();
        for(Integer inventoryId : inventoryToDeviceMap.keySet()) {
            Integer deviceId = inventoryToDeviceMap.get(inventoryId);
            //can't map one-ways in any meaningful way, since all their device ids are 0
            if(deviceId != 0) {
                deviceToInventoryMap.put(deviceId, inventoryId);
            }
        }
        this.deviceToInventoryMap = deviceToInventoryMap;
    }
    
    /**
     * Set the PaoType to be used for all PaoIdentifiers returned.
     * In the future we may want a map of PaoType to deviceId.
     */
    public void setPaoType(PaoType paoType) {
        this.paoType = paoType;
    }
    
    @Override
    public Multimap<Integer, PaoIdentifier> getRelayToDeviceMapByLoadGroups(Iterable<Integer> loadGroupIds) {
        Set<Integer> deviceIds = Sets.newHashSet();
        for(Integer loadGroupId : loadGroupIds) {
            Collection<Integer> inventory = loadGroupToInventoryMap.get(loadGroupId);
            for(Integer inventoryId : inventory) {
                deviceIds.add(inventoryToDeviceMap.get(inventoryId));
            }
        }
        return getRelayToDeviceMapByDeviceIds(deviceIds);
    }

    @Override
    public Multimap<Integer, PaoIdentifier> getRelayToDeviceMapByDeviceIds(Iterable<Integer> deviceIds) {
        Multimap<Integer, PaoIdentifier> relayToDeviceMap = ArrayListMultimap.create();
        for(Integer deviceId : deviceIds) {
            if(deviceId != 0) {
                Integer inventoryId = deviceToInventoryMap.get(deviceId);
                Map<Integer, Integer> relayToApplianceMap = inventoryRelayApplianceMap.get(inventoryId);
                for(Integer relay : relayToApplianceMap.keySet()) {
                    relayToDeviceMap.put(relay, new PaoIdentifier(deviceId, paoType));
                }
            }
        }
        return relayToDeviceMap;
    }

    @Override
    public DeviceRelayApplianceCategories getDeviceRelayApplianceCategoryId(Iterable<Integer> inventoryIds) {
        Set<DeviceRelayApplianceCategory> dracs = Sets.newHashSet();
        for(Integer inventoryId : inventoryIds) {
            Map<Integer, Integer> relayAppliances = inventoryRelayApplianceMap.get(inventoryId);
            int deviceId = inventoryToDeviceMap.get(inventoryId);
            for(Integer relay : relayAppliances.keySet()) {
                Integer appliance = relayAppliances.get(relay);
                DeviceRelayApplianceCategory drac = new DeviceRelayApplianceCategory(deviceId, relay, appliance);
                dracs.add(drac);
            }
        }
        return new DeviceRelayApplianceCategories(dracs);
    }

    @Override
    public InventoryRelayAppliances getInventoryRelayAppliances(Iterable<Integer> inventoryIds) {
        Set<InventoryRelayAppliance> inventoryRelayApplianceSet = Sets.newHashSet();
        for(Integer inventoryId : inventoryIds) {
            Map<Integer, Integer> relayApplianceMap = inventoryRelayApplianceMap.get(inventoryId);
            for(Integer relay : relayApplianceMap.keySet()) {
                //currently using as appliance and applianceCatId (this applianceId is never used)
                Integer applianceCatId = relayApplianceMap.get(relay);
                InventoryRelayAppliance inventoryRelayAppliance = 
                        new InventoryRelayAppliance(inventoryId, relay, applianceCatId, applianceCatId);
                inventoryRelayApplianceSet.add(inventoryRelayAppliance);
            }
        }
        return new InventoryRelayAppliances(inventoryRelayApplianceSet);
    }

    @Override
    public Multimap<Integer, Integer> getInventoryApplianceMap(Iterable<Integer> inventoryIds) {
        Multimap<Integer, Integer> filteredInventoryToApplianceMap = ArrayListMultimap.create();
        for(Integer inventoryId : inventoryIds) {
            Collection<Integer> applianceIds = inventoryRelayApplianceMap.get(inventoryId).values();
            filteredInventoryToApplianceMap.putAll(inventoryId, applianceIds);
        }
        return filteredInventoryToApplianceMap;
    }
    
    /*
     * Unimplemented methods:
     */
    
    @Override
    public void add(LMHardwareConfiguration lmHardwareConfiguration) {
        throw new MethodNotImplementedException();
    }

    @Override
    public void delete(LMHardwareConfiguration lmHardwareConfiguration) {
        throw new MethodNotImplementedException();
    }

    @Override
    public void update(LMHardwareConfiguration lmHardwareConfiguration) {
        throw new MethodNotImplementedException();
    }

    @Override
    public void delete(int invetoryId) {
        throw new MethodNotImplementedException();
    }

    @Override
    public void delete(Collection<Integer> inventoryIds) {
        throw new MethodNotImplementedException();
    }

    @Override
    public void deleteForAppliance(int applianceId) {
        throw new MethodNotImplementedException();
    }

    @Override
    public void deleteForAppliances(Collection<Integer> applianceIds) {
        throw new MethodNotImplementedException();
    }

    @Override
    public LMHardwareConfiguration getStaticLoadGroupMapping(LiteAccountInfo liteAcct,
                                                             LiteLmHardwareBase lmHw,
                                                             LiteStarsEnergyCompany energyCompany) {
        throw new MethodNotImplementedException();
    }

    @Override
    public List<LMHardwareConfiguration> getForInventoryId(int inventoryId) {
        throw new MethodNotImplementedException();
    }

    @Override
    public Map<Integer, Integer> getDeviceIdToRelayMapByLoadGroups(Iterable<Integer> loadGroupIds) {
        throw new MethodNotImplementedException();
    }

}
