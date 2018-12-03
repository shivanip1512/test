package com.cannontech.dr.assetavailability.service.impl;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.model.SortingParameters;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.dr.assetavailability.AllRelayCommunicationTimes;
import com.cannontech.dr.assetavailability.ApplianceAssetAvailabilityDetails;
import com.cannontech.dr.assetavailability.ApplianceAssetAvailabilitySummary;
import com.cannontech.dr.assetavailability.ApplianceWithRuntime;
import com.cannontech.dr.assetavailability.AssetAvailabilityCombinedStatus;
import com.cannontech.dr.assetavailability.AssetAvailabilityDetails;
import com.cannontech.dr.assetavailability.AssetAvailabilityStatus;
import com.cannontech.dr.assetavailability.AssetAvailabilitySummary;
import com.cannontech.dr.assetavailability.DeviceRelayApplianceCategories;
import com.cannontech.dr.assetavailability.InventoryRelayAppliances;
import com.cannontech.dr.assetavailability.SimpleAssetAvailability;
import com.cannontech.dr.assetavailability.dao.AssetAvailabilityDao;
import com.cannontech.dr.assetavailability.dao.DRGroupDeviceMappingDao;
import com.cannontech.dr.assetavailability.dao.DynamicLcrCommunicationsDao;
import com.cannontech.dr.assetavailability.service.AssetAvailabilityService;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.dr.hardware.dao.LMHardwareConfigurationDao;
import com.cannontech.stars.dr.optout.dao.OptOutEventDao;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.user.YukonUserContext;
import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

public class AssetAvailabilityServiceImpl implements AssetAvailabilityService {
    private static final Logger log = YukonLogManager.getLogger(AssetAvailabilityServiceImpl.class);
    private final OptOutEventDao optOutEventDao;
    private final LMHardwareConfigurationDao lmHardwareConfigurationDao;
    private final InventoryDao inventoryDao;
    private final DRGroupDeviceMappingDao drGroupDeviceMappingDao;
    private final GlobalSettingDao globalSettingDao;
    private final DynamicLcrCommunicationsDao lcrCommunicationsDao;
    private final AssetAvailabilityDao assetAvailabilityDao;
    
    @Autowired
    public AssetAvailabilityServiceImpl(OptOutEventDao optOutEventDao,
                                        LMHardwareConfigurationDao lmHardwareConfigurationDao, 
                                        InventoryDao inventoryDao,
                                        DRGroupDeviceMappingDao drGroupDeviceMappingDao,
                                        GlobalSettingDao globalSettingDao,
                                        DynamicLcrCommunicationsDao lcrCommunicationsDao,
                                        AssetAvailabilityDao assetAvailabilityDao) {
        this.optOutEventDao = optOutEventDao;
        this.lmHardwareConfigurationDao = lmHardwareConfigurationDao;
        this.inventoryDao = inventoryDao;
        this.drGroupDeviceMappingDao = drGroupDeviceMappingDao;
        this.globalSettingDao = globalSettingDao;
        this.lcrCommunicationsDao = lcrCommunicationsDao;
        this.assetAvailabilityDao = assetAvailabilityDao;
    }
    
    @Override
    public AssetAvailabilitySummary getAssetAvailabilityFromDrGroup(PaoIdentifier drPaoIdentifier) {
        log.debug("Calculating asset availability summary for " + drPaoIdentifier.getPaoType() + " "
            + drPaoIdentifier.getPaoId());

        Instant now = Instant.now();
        DateTime communicatingWindowEnd = now.minus(getCommunicationWindowDuration()).toDateTime();
        DateTime runtimeWindowEnd = now.minus(getRuntimeWindowDuration()).toDateTime();
        
        Set<Integer> loadGroupIds = drGroupDeviceMappingDao.getLoadGroupIdsForDrGroup(drPaoIdentifier);
        AssetAvailabilitySummary assetAvailabilitySummary =
            assetAvailabilityDao.getAssetAvailabilitySummary(loadGroupIds, communicatingWindowEnd.toInstant(),
                runtimeWindowEnd.toInstant(), now);

        return assetAvailabilitySummary;
    }
 
    @Override
    public ApplianceAssetAvailabilitySummary getApplianceAssetAvailability(PaoIdentifier drPaoIdentifier) {
        log.debug("Calculating appliance-level asset availability summary for " + drPaoIdentifier.getPaoType() 
                + " " + drPaoIdentifier.getPaoId());
        Instant now = Instant.now();
        Instant communicatingWindowEnd = now.minus(getCommunicationWindowDuration());
        Instant runtimeWindowEnd = now.minus(getRuntimeWindowDuration());
        
        ApplianceAssetAvailabilitySummary summary =
            assetAvailabilityDao.getApplianceAssetAvailabilitySummary(drPaoIdentifier, communicatingWindowEnd,
                runtimeWindowEnd,now);
        return summary;
   }
   
    @Override
    public SimpleAssetAvailability getAssetAvailability(int inventoryId) {
        log.debug("Calculating simple asset availability for inventory " + inventoryId);
        Map<Integer, SimpleAssetAvailability> singleValueMap = getAssetAvailability(Sets.newHashSet(inventoryId));
        return singleValueMap.get(inventoryId);
    }
    
    @Override
    public Set<SimpleDevice> getUnavailableDevicesInDrGrouping(YukonPao yukonPao) {
        Map<Integer, SimpleDevice> inventoryPaoMap = drGroupDeviceMappingDao.getInventoryPaoMapForGrouping(yukonPao.getPaoIdentifier());
        Map<Integer, SimpleAssetAvailability> availabilities = getAssetAvailability(inventoryPaoMap.keySet());
        
        Set<SimpleDevice> unavailablePaos = new HashSet<>();
        for (Map.Entry<Integer, SimpleAssetAvailability> entry : availabilities.entrySet()) {
            SimpleAssetAvailability availability = entry.getValue();
            if (availability.getStatus() == AssetAvailabilityStatus.UNAVAILABLE) {
                Integer inventoryId = entry.getKey();
                SimpleDevice unavailableDevice = inventoryPaoMap.get(inventoryId);
                unavailablePaos.add(unavailableDevice);
            }
        }
        return unavailablePaos;
    }
    
    @Override
    public Map<Integer, SimpleAssetAvailability> getAssetAvailability(Iterable<Integer> inventoryIds) {
        log.debug("Calculating asset availability total for inventoryIds");
        if(log.isTraceEnabled()) {
            log.trace("InventoryIds: " + Joiner.on(", ").join(inventoryIds));
        }
        log.debug("Asset availability: getDeviceIds");
        Map<Integer, Integer> inventoryAndDevices = inventoryDao.getDeviceIds(inventoryIds);
        log.debug("Asset availability: getDeviceIds complete");
        Set<Integer> oneWayInventory = getOneWayInventory(inventoryAndDevices);
        Set<Integer> deviceIds = getTwoWayInventoryDevices(inventoryAndDevices);
        
        //Get opted out inventory
        Set<Integer> optedOutInventory = optOutEventDao.getOptedOutInventory(inventoryIds);
        
        //Only continue if there are two-way devices to analyze
        Map<Integer, AllRelayCommunicationTimes> communicationTimes = null;
        if(!oneWayInventory.equals(inventoryAndDevices.keySet())) {
            communicationTimes = lcrCommunicationsDao.findAllRelayCommunicationTimes(deviceIds);
        }
        //build the asset availability objects
        Map<Integer, SimpleAssetAvailability> assetAvailabilityMap = 
                buildAssetAvailabilityMap(inventoryIds, optedOutInventory, oneWayInventory, inventoryAndDevices, 
                                          communicationTimes);
        
        log.debug("Done calculating asset availability total for inventoryIds");
        return assetAvailabilityMap;
    }
    
    @Override
    public SearchResults<ApplianceAssetAvailabilityDetails> getAssetAvailabilityWithAppliance(PaoIdentifier paoIdentifier,
            PagingParameters paging, AssetAvailabilityCombinedStatus[] filters, SortingParameters sortBy,
            YukonUserContext userContext) {
        log.debug("Calculating asset availability for " + paoIdentifier.getPaoType() + " " + paoIdentifier.getPaoId());
        Set<Integer> loadGroupIds = drGroupDeviceMappingDao.getLoadGroupIdsForDrGroup(paoIdentifier);

        Instant now = Instant.now();
        Instant communicatingWindowEnd = now.minus(getCommunicationWindowDuration());
        Instant runtimeWindowEnd = now.minus(getRuntimeWindowDuration());

        SearchResults<ApplianceAssetAvailabilityDetails> assetAvailabilityDetails =
            assetAvailabilityDao.getAssetAvailabilityDetailsWithAppliance(loadGroupIds, paging, filters, sortBy,
                communicatingWindowEnd, runtimeWindowEnd, now, userContext);

        return assetAvailabilityDetails;
    }

    @Override
    public SearchResults<AssetAvailabilityDetails> getAssetAvailabilityDetails(List<DeviceGroup> subGroups,
            PaoIdentifier paoIdentifier, PagingParameters paging, AssetAvailabilityCombinedStatus[] filters,
            SortingParameters sortBy, YukonUserContext userContext) {
        log.debug("Calculating asset availability for " + paoIdentifier.getPaoType() + " " + paoIdentifier.getPaoId());
        Set<Integer> loadGroupIds = drGroupDeviceMappingDao.getLoadGroupIdsForDrGroup(paoIdentifier);

        Instant now = Instant.now();
        Instant communicatingWindowEnd = now.minus(getCommunicationWindowDuration());
        Instant runtimeWindowEnd = now.minus(getRuntimeWindowDuration());

        SearchResults<AssetAvailabilityDetails> assetAvailabilityDetails =
            assetAvailabilityDao.getAssetAvailabilityDetails(subGroups, loadGroupIds, paging, filters, sortBy,
                communicatingWindowEnd, runtimeWindowEnd, now, userContext);

        return assetAvailabilityDetails;
    }

    /**
     * From the supplied sets and maps, this method determines the asset availability of each inventory and returns a
     * map of inventoryId to SimpleAssetAvailability of that inventory.
     * One-way inventory are considered to be always communicating and running, but the communication time and runtimes
     * on their SimpleAssetAvailability object will be null.
     * Two-way inventory may also have null communication time and/or an empty set of runtimes if no communication or
     * runtime data has ever been collected for the device.
     */
    private Map<Integer, SimpleAssetAvailability> buildAssetAvailabilityMap(Iterable<Integer> inventoryIds, 
            Collection<Integer> optedOutInventory, Collection<Integer> oneWayInventory, 
            Map<Integer, Integer> inventoryAndDevices, Map<Integer, AllRelayCommunicationTimes> communicationTimes) {
        
        InventoryRelayAppliances inventoryRelayAppliances = lmHardwareConfigurationDao.getInventoryRelayAppliances(inventoryIds);
        DeviceRelayApplianceCategories dracs = lmHardwareConfigurationDao.getDeviceRelayApplianceCategoryId(inventoryIds);
        Multimap<Integer, Integer> oneWayInventoryApplianceCategoryIds = lmHardwareConfigurationDao.getInventoryApplianceMap(oneWayInventory);
        Map<Integer, SimpleAssetAvailability> assetAvailabilityMap = Maps.newHashMap();
        Instant now = Instant.now();
        for(int inventoryId : inventoryIds) {
            boolean isOptedOut = optedOutInventory.contains(inventoryId);
            
            if(oneWayInventory.contains(inventoryId)) {
                Collection<Integer> applianceCategoryIds = oneWayInventoryApplianceCategoryIds.get(inventoryId);
                SimpleAssetAvailability assetAvailability = new SimpleAssetAvailability(inventoryId, isOptedOut, applianceCategoryIds);
                assetAvailabilityMap.put(inventoryId, assetAvailability);
            } else {
                int deviceId = inventoryAndDevices.get(inventoryId);
                AllRelayCommunicationTimes times = communicationTimes.get(deviceId);
                Instant lastCommunicationTime = null;
                Map<Integer, Instant> relayRuntimes = null;
                if(times != null) {
                    lastCommunicationTime = times.getLastCommunicationTime();
                    relayRuntimes = times.getRelayRuntimeMap();
                }
                Map<Integer, Integer> relayApplianceMap = inventoryRelayAppliances.getRelayApplianceMap(inventoryId);
                Set<ApplianceWithRuntime> applianceRuntimes = Sets.newHashSet();
                if(relayApplianceMap != null) {
                    for(Integer relay : relayApplianceMap.keySet()) {
                        if(relayRuntimes != null && relayRuntimes.containsKey(relay)) {
                            //runtime exists for this relay
                            Instant runtime = relayRuntimes.get(relay);
                            int applianceCategoryId = dracs.getApplianceCategoryId(deviceId, relay);
                            applianceRuntimes.add(new ApplianceWithRuntime(applianceCategoryId, runtime));
                        } else {
                            //no runtime for this relay - insert appliance category and null runtime
                            int applianceCategoryId = inventoryRelayAppliances.getApplianceCategoryId(inventoryId, relay);
                            applianceRuntimes.add(new ApplianceWithRuntime(applianceCategoryId, null));
                        }
                    }
                }
                
                AssetAvailabilityStatus status = getStatus(now, lastCommunicationTime, applianceRuntimes);
                SimpleAssetAvailability assetAvailability = new SimpleAssetAvailability(inventoryId, status, isOptedOut, 
                                                               lastCommunicationTime, applianceRuntimes);
                assetAvailabilityMap.put(inventoryId, assetAvailability);
            }
        }
        return assetAvailabilityMap;
    }
    
    /**
     * Given the map of inventoryIds to deviceIds, this method returns the subset of inventoryIds that belong to
     * one-way inventory. These are identified by their mapping to deviceId 0.
     */
    private Set<Integer> getOneWayInventory(Map<Integer, Integer> inventoryAndDevices) {
        Set<Integer> oneWayInventory = Sets.newHashSet();
        for(Map.Entry<Integer, Integer> entry : inventoryAndDevices.entrySet()) {
            //1-way inventory are mapped to device id 0
            if(entry.getValue() == 0) {
                oneWayInventory.add(entry.getKey());
            }
        }
        return oneWayInventory;
    }
    
    /**
     * Given the map of inventoryIds to deviceIds, this method returns the subset of deviceIds that belong to
     * two-way inventory.
     */
    private Set<Integer> getTwoWayInventoryDevices(Map<Integer, Integer> inventoryAndDevices) {
        Set<Integer> deviceIds = Sets.newHashSet(inventoryAndDevices.values());
        deviceIds.remove(0); //Remove system device (possibly in this list due to 1-way inventory)
        return deviceIds;
    }
    
    /**
     * Gets an AssetAvailabilityStatus by comparing the current instant to the instant of last communication time and
     * the instants of last runtime(s). The device is communicating if the last communication time is within
     * LAST_COMMUNICATION_HOURS of the current time, and the device is running if any of the non-zero runtime values
     * was reported within LAST_RUNTIME_HOURS of the current time.
     */
    private AssetAvailabilityStatus getStatus(Instant now, Instant lastCommunicationTime, 
                                              Collection<ApplianceWithRuntime> applianceRuntimes) {
        boolean communicating = false;
        boolean hasRuntime = false;
        Instant endOfCommunicationWindow = now.minus(getCommunicationWindowDuration());
        if(lastCommunicationTime != null && lastCommunicationTime.isAfter(endOfCommunicationWindow)) {
            communicating = true;
        }
        Instant endOfRuntimeWindow = now.minus(getRuntimeWindowDuration());
        for(ApplianceWithRuntime applianceRuntime : applianceRuntimes) {
            Instant runtime = applianceRuntime.getLastNonZeroRuntime();
            if(runtime != null && runtime.isAfter(endOfRuntimeWindow)) {
                hasRuntime = true;
                break;
            }
        }
        if(communicating) {
            if(hasRuntime) {
                return AssetAvailabilityStatus.ACTIVE;
            }
            return AssetAvailabilityStatus.INACTIVE;
        }
        return AssetAvailabilityStatus.UNAVAILABLE;
    }
    
    private Duration getCommunicationWindowDuration() {
        return Duration.standardHours(globalSettingDao.getInteger(GlobalSettingType.LAST_COMMUNICATION_HOURS));
    }
    
    private Duration getRuntimeWindowDuration() {
        return Duration.standardHours(globalSettingDao.getInteger(GlobalSettingType.LAST_RUNTIME_HOURS));
    }

}
