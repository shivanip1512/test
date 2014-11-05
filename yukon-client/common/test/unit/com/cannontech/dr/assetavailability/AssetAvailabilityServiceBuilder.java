package com.cannontech.dr.assetavailability;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import org.joda.time.Instant;

import com.cannontech.common.mock.MockGlobalSettingDao;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.dr.assetavailability.dao.MockAssetAvailabilityDao;
import com.cannontech.dr.assetavailability.dao.MockDrGroupDeviceMappingDao;
import com.cannontech.dr.assetavailability.dao.MockLcrCommunicationsDao;
import com.cannontech.dr.assetavailability.service.AssetAvailabilityService;
import com.cannontech.dr.assetavailability.service.impl.AssetAvailabilityServiceImpl;
import com.cannontech.stars.dr.hardware.dao.MockInventoryDao;
import com.cannontech.stars.dr.hardware.dao.MockLmHardwareConfigurationDao;
import com.cannontech.stars.dr.optout.dao.MockOptOutEventDao;
import com.cannontech.system.GlobalSettingType;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

public class AssetAvailabilityServiceBuilder {
    private Integer communicationHours = null;
    private Integer runtimeHours = null;
    private Multimap<Integer, Integer> loadGroupToInventoryMap = ArrayListMultimap.create();
    private Set<Integer> optedOutInventory = Sets.newHashSet();
    private Map<Integer, Integer> inventoryToDeviceMap = Maps.newHashMap();
    private Multimap<PaoIdentifier, Integer> drGroupToLoadGroupMap = ArrayListMultimap.create();
    private Map<Integer, Map<Integer, Integer>> inventoryRelayApplianceMap = Maps.newHashMap();
    private Map<Integer, AssetAvailabilityPointDataTimes> data = Maps.newHashMap();
    
    public AssetAvailabilityService build() {
        //Opt Outs
        MockOptOutEventDao optOutEventDao = new MockOptOutEventDao(loadGroupToInventoryMap, optedOutInventory);
        
        //LM Hardware Configuration
        MockLmHardwareConfigurationDao lmHardwareConfigurationDao = new MockLmHardwareConfigurationDao(loadGroupToInventoryMap,
                                                                                                       inventoryToDeviceMap,
                                                                                                       inventoryRelayApplianceMap);
        
        //Inventory
        MockInventoryDao inventoryDao = new MockInventoryDao();
        inventoryDao.setInventoryToDeviceMap(inventoryToDeviceMap);
        
        //DR Group Device Mapping
        MockDrGroupDeviceMappingDao drGroupDeviceMappingDao = new MockDrGroupDeviceMappingDao(loadGroupToInventoryMap,
                                                                                              inventoryToDeviceMap,
                                                                                              drGroupToLoadGroupMap);
        
        //Global Settings
        MockGlobalSettingDao globalSettingDao = new MockGlobalSettingDao();
        if(communicationHours != null) {
            globalSettingDao.addValue(GlobalSettingType.LAST_COMMUNICATION_HOURS, communicationHours);
        }
        if(runtimeHours != null) {
            globalSettingDao.addValue(GlobalSettingType.LAST_RUNTIME_HOURS, runtimeHours);
        }
        
        //LCR Communications
        MockLcrCommunicationsDao lcrCommunicationsDao = new MockLcrCommunicationsDao(data);
        
        MockAssetAvailabilityDao mockAssetAvailabilityDao = new MockAssetAvailabilityDao();
        
        AssetAvailabilityServiceImpl assetAvailabilityService = new AssetAvailabilityServiceImpl(optOutEventDao, 
                                                                                                 lmHardwareConfigurationDao, 
                                                                                                 inventoryDao,
                                                                                                 drGroupDeviceMappingDao, 
                                                                                                 globalSettingDao,
                                                                                                 lcrCommunicationsDao,
                                                                                                 mockAssetAvailabilityDao);
        return assetAvailabilityService;
    }
    
    public AssetAvailabilityServiceBuilder withCommunicationHours(int hours) {
        communicationHours = hours;
        return this;
    }
    
    public AssetAvailabilityServiceBuilder withRuntimeHours(int hours) {
        runtimeHours = hours;
        return this;
    }
    
    /**
     * Maps Load Groups to enrolled inventory.
     * Calling this method more than once will append data.
     * 
     * Example: A load group with id 1, containing inventory with ids 10, 11, and 12.
     * 
     * .withLoadGroupInventory(1, new Integer[]{10, 11, 12})
     */
    public AssetAvailabilityServiceBuilder withLoadGroupInventory(int loadGroupId, Integer... inventoryIds) {
        loadGroupToInventoryMap.putAll(loadGroupId, Arrays.asList(inventoryIds));
        return this;
    }
    
    /**
     * Maps Control Areas, Scenarios, Programs, and Load Groups to the Load Groups they contain.
     * Calling this method more than once will append data.
     * 
     * Example: A control area with id 123, containing load groups with ids 1, 2 and 3.
     * 
     * .withDrGroupToLoadGroupIds(new PaoIdentifier(123, PaoType.LM_CONTROL_AREA), 1, 2, 3)
     */
    public AssetAvailabilityServiceBuilder withDrGroupToLoadGroupIds(PaoIdentifier paoIdentifier, 
                                                                     Integer... loadGroupIds) {
        drGroupToLoadGroupMap.putAll(paoIdentifier, Arrays.asList(loadGroupIds));
        return this;
    }
    
    /**
     * Opts out inventory.
     * Calling this method more than once will append data.
     */
    public AssetAvailabilityServiceBuilder withOptedOutInventory(Integer... inventoryIds) {
        optedOutInventory.addAll(Arrays.asList(inventoryIds));
        return this;
    }
    
    /**
     * Maps inventoryIds to deviceIds.
     * Calling this method more than once appends data.
     */
    public AssetAvailabilityServiceBuilder withInventoryToDeviceMapping(Integer inventoryId, Integer deviceId) {
        inventoryToDeviceMap.put(inventoryId, deviceId);
        return this;
    }
    
    /**
     * Maps a single inventory to relay to appliance.
     * Calling this method more than once appends data.
     */
    public AssetAvailabilityServiceBuilder withInventoryRelayApplianceMapping(Integer inventoryId, Integer relay, 
                                                                           Integer applianceId) {
        if(inventoryRelayApplianceMap.containsKey(inventoryId)) {
            inventoryRelayApplianceMap.get(inventoryId).put(relay, applianceId);
        } else {
            Map<Integer, Integer> relayAppliance = Maps.newHashMap();
            relayAppliance.put(relay, applianceId);
            inventoryRelayApplianceMap.put(inventoryId, relayAppliance);
        }
        return this;
    }
    
    /**
     * Maps an inventory to one or more (relay + appliance)
     * Calling this method more than once appends data.
     * 
     * Example: On inventory with id 1, mapping applianceId 52 to relay 2, applianceId 53 to relay 3, and applianceId
     * 54 to relay 4.
     * 
     * .withInventoryRelayApplianceMapping(1, new Integer[][] {
     *     {2, 52},
     *     {3, 53},
     *     {4, 54}
     * })
     */
    public AssetAvailabilityServiceBuilder withInventoryRelayApplianceMapping(Integer inventoryId,
                                                                           Integer[][] relayApplianceArray) {
        if(inventoryRelayApplianceMap.containsKey(inventoryId)) {
            for(Integer[] relayAppliance : relayApplianceArray) {
                inventoryRelayApplianceMap.get(inventoryId).put(relayAppliance[0], relayAppliance[1]);
            }
        } else {
            Map<Integer, Integer> relayAppliances = Maps.newHashMap();
            for(Integer[] relayAppliance : relayApplianceArray) {
                relayAppliances.put(relayAppliance[0], relayAppliance[1]);
            }
            inventoryRelayApplianceMap.put(inventoryId, relayAppliances);
        }
        return this;
    }
    
    public AssetAvailabilityServiceBuilder withData(int deviceId, Instant communicationTime, Instant relay1runtime, 
                                                    Instant relay2runtime, Instant relay3runtime, Instant relay4runtime) {
        AssetAvailabilityPointDataTimes times = new AssetAvailabilityPointDataTimes(deviceId);
        times.setLastCommunicationTime(communicationTime);
        times.setRelayRuntime(1, relay1runtime);
        times.setRelayRuntime(2, relay2runtime);
        times.setRelayRuntime(3, relay3runtime);
        times.setRelayRuntime(4, relay4runtime);
        data.put(deviceId, times);
        return this;
    }
}