package com.cannontech.dr.assetavailability.service.impl;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.model.SortingParameters;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.dr.assetavailability.AllRelayCommunicationTimes;
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
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.HashBiMap;
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
    @Autowired private AssetAvailabilityDao assetAvailabilityDao;
    
    @Autowired
    public AssetAvailabilityServiceImpl(OptOutEventDao optOutEventDao,
                                        LMHardwareConfigurationDao lmHardwareConfigurationDao, 
                                        InventoryDao inventoryDao,
                                        DRGroupDeviceMappingDao drGroupDeviceMappingDao,
                                        GlobalSettingDao globalSettingDao,
                                        DynamicLcrCommunicationsDao lcrCommunicationsDao) {
        this.optOutEventDao = optOutEventDao;
        this.lmHardwareConfigurationDao = lmHardwareConfigurationDao;
        this.inventoryDao = inventoryDao;
        this.drGroupDeviceMappingDao = drGroupDeviceMappingDao;
        this.globalSettingDao = globalSettingDao;
        this.lcrCommunicationsDao = lcrCommunicationsDao;
    }
    
    @Override
    public AssetAvailabilitySummary getAssetAvailabilityFromDrGroup(PaoIdentifier drPaoIdentifier) {
        log.debug("Calculating asset availability summary for " + drPaoIdentifier.getPaoType() + " "
            + drPaoIdentifier.getPaoId());

        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

        Instant now = Instant.now();
        String currentTime = formatter.print(now);

        DateTime communicatingWindowEnd = now.minus(getCommunicationWindowDuration()).toDateTime();
        String communicatingWindowEndFormatted = formatter.print(communicatingWindowEnd);

        DateTime runtimeWindowEnd = now.minus(getRuntimeWindowDuration()).toDateTime();
        String runtimeWindowEndFormatted = formatter.print(runtimeWindowEnd);
        Set<Integer> loadGroupIds = drGroupDeviceMappingDao.getLoadGroupIdsForDrGroup(drPaoIdentifier);
        AssetAvailabilitySummary assetAvailabilitySummary =
            assetAvailabilityDao.getAssetAvailabilitySummary(loadGroupIds, communicatingWindowEndFormatted,
                runtimeWindowEndFormatted, currentTime);

        return assetAvailabilitySummary;
    }
    
    
    
    
    @Override
    public ApplianceAssetAvailabilitySummary getApplianceAssetAvailability(PaoIdentifier drPaoIdentifier) {
        log.debug("Calculating appliance-level asset availability summary for " + drPaoIdentifier.getPaoType() 
                + " " + drPaoIdentifier.getPaoId());
        Set<Integer> loadGroupIds = drGroupDeviceMappingDao.getLoadGroupIdsForDrGroup(drPaoIdentifier);
        return getApplianceAssetAvailability(loadGroupIds);
    }
    
    @Override
    public ApplianceAssetAvailabilitySummary getApplianceAssetAvailability(Iterable<Integer> loadGroupIds) {
        log.debug("Calculating appliance-level asset availability summary for load groups");
        if(log.isTraceEnabled()) {
            log.trace("Load group ids: " + Joiner.on(", ").join(loadGroupIds));
        }
        //Get all inventory & associated paos
        Map<Integer, Integer> inventoryAndDevices = drGroupDeviceMappingDao.getInventoryAndDeviceIdsForLoadGroups(loadGroupIds);
        Set<Integer> inventoryIds = inventoryAndDevices.keySet();
        
        //Get appliances
        InventoryRelayAppliances inventoryRelayAppliances = lmHardwareConfigurationDao.getInventoryRelayAppliances(inventoryIds);
        Multimap<Integer, Integer> inventoryToApplianceMultimap = inventoryRelayAppliances.getInventoryToApplianceMultimap();
        Set<Integer> allApplianceIds = Sets.newHashSet(inventoryToApplianceMultimap.values());
        ApplianceAssetAvailabilitySummary summary = new ApplianceAssetAvailabilitySummary(allApplianceIds);
        
        //Get opted out appliances
        Set<Integer> optedOutInventory = optOutEventDao.getOptedOutInventoryByLoadGroups(loadGroupIds);
        Set<Integer> optedOutAppliances = getAppliancesFromInventory(inventoryToApplianceMultimap, optedOutInventory);
        summary.addOptedOut(optedOutAppliances);
        
        //1-way inventory (and attached appliances) are always considered communicating and running
        Set<Integer> oneWayInventory = getOneWayInventory(inventoryAndDevices);
        Set<Integer> oneWayAppliances = getAppliancesFromInventory(inventoryToApplianceMultimap, oneWayInventory);
        summary.addCommunicating(oneWayAppliances);
        summary.addRunning(oneWayAppliances);
        
        //remove 1-way inventory from inventoryAndDevices map
        Predicate<Integer> notOneWay = Predicates.not(Predicates.in(oneWayInventory));
        inventoryAndDevices = Maps.filterKeys(inventoryAndDevices, notOneWay);
        
        //If all devices are one-way, we don't need to check any points for running/communicating
        if(inventoryAndDevices.isEmpty()) {
            return summary;
        }
        
        //Get last communication and runtimes for 2-way devices
        Set<Integer> twoWayDevices = Sets.newHashSet(inventoryAndDevices.values());
        Map<Integer, AllRelayCommunicationTimes> allTimes = lcrCommunicationsDao.findAllRelayCommunicationTimes(twoWayDevices);
        
        //Get communications window
        Instant now = Instant.now();
        Instant communicatingWindowEnd = now.minus(getCommunicationWindowDuration());
        
        //Determine if devices are in communications window
        Set<Integer> communicatedDevices = new HashSet<>();
        for(Map.Entry<Integer, AllRelayCommunicationTimes> entry : allTimes.entrySet()) {
            AllRelayCommunicationTimes times = entry.getValue();
            if(times != null) {
                Instant lastCommunicationTime = times.getLastCommunicationTime();
                if (lastCommunicationTime != null && lastCommunicationTime.isAfter(communicatingWindowEnd)) {
                    communicatedDevices.add(entry.getKey());
                }
            }
        }
        
        Map<Integer, Integer> devicesAndInventory = HashBiMap.create(inventoryAndDevices).inverse();
        
        //Add communicating appliances based on inventory
        Set<Integer> communicatedInventory = getInventoryFromDevices(communicatedDevices, devicesAndInventory);
        Set<Integer> communicatedAppliances = getAppliancesFromInventory(inventoryToApplianceMultimap, communicatedInventory);
        summary.addCommunicating(communicatedAppliances);
        
        //Get runtime window
        Instant runtimeWindowEnd = now.minus(getRuntimeWindowDuration());
        
        //Get appliances in runtime window
        Set<Integer> runningAppliances = new HashSet<>();
        for(Map.Entry<Integer, Integer> entry : inventoryAndDevices.entrySet()) {
            int deviceId = entry.getValue();
            int inventoryId = entry.getKey();
            AllRelayCommunicationTimes times = allTimes.get(deviceId);
            if(times != null) {
                Map<Integer, Integer> relayApplianceMap = inventoryRelayAppliances.getRelayApplianceMap(inventoryId);
                Map<Integer, Instant> relayRuntimes = times.getRelayRuntimeMap();
                if(relayApplianceMap != null) {
                    for(Integer relay : relayApplianceMap.keySet()) {
                        if(relayRuntimes != null && relayRuntimes.get(relay) != null && 
                           relayRuntimes.get(relay).isAfter(runtimeWindowEnd)) {
                            //runtime exists for this relay, but is it in the window?
                            Integer applianceId = inventoryRelayAppliances.getApplianceId(inventoryId, relay);
                            runningAppliances.add(applianceId);
                        }
                    }
                }
            }
        }
        summary.addRunning(runningAppliances);
        
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
    public List<AssetAvailabilityDetails> getAssetAvailability(PaoIdentifier paoIdentifier, PagingParameters paging,
            AssetAvailabilityCombinedStatus[] filters, SortingParameters sortBy) {
        log.debug("Calculating asset availability for " + paoIdentifier.getPaoType() + " " + paoIdentifier.getPaoId());
        Set<Integer> loadGroupIds = drGroupDeviceMappingDao.getLoadGroupIdsForDrGroup(paoIdentifier);

        String sortingOrder = (sortBy == null) ? "SERIAL_NUM" : sortBy.getSort();
        String sortingDirection = ((sortBy == null) ? (Direction.asc).name() : sortBy.getDirection().toString());

        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

        Instant now = Instant.now();
        String currentTime = formatter.print(now);

        DateTime communicatingWindowEnd = now.minus(getCommunicationWindowDuration()).toDateTime();
        String communicatingWindowEndFormatted = formatter.print(communicatingWindowEnd);

        DateTime runtimeWindowEnd = now.minus(getRuntimeWindowDuration()).toDateTime();
        String runtimeWindowEndFormatted = formatter.print(runtimeWindowEnd);

        String filterString = convertFilterListToString(filters);

        List<AssetAvailabilityDetails> assetAvailabilityDetails =
            assetAvailabilityDao.getAssetAvailabilityDetails(loadGroupIds, paging, filterString, sortingOrder,
                sortingDirection, communicatingWindowEndFormatted, runtimeWindowEndFormatted, currentTime);
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
    
    /**
     * Uses the InventoryToApplianceMultimap to get the set of all appliances attached to the specified inventory.
     */
    private Set<Integer> getAppliancesFromInventory(Multimap<Integer, Integer> inventoryToApplianceMultimap, Iterable<Integer> inventoryIds) {
        Set<Integer> applianceIds = Sets.newHashSet();
        for(int inventoryId : inventoryIds) {
            applianceIds.addAll(inventoryToApplianceMultimap.get(inventoryId));
        }
        return applianceIds;
    }
    
    private String convertFilterListToString(AssetAvailabilityCombinedStatus[] filters) {
        final StringBuilder filterCriteria = new StringBuilder();
        if (null != filters) {
            for (final AssetAvailabilityCombinedStatus status : filters) {
                if (filterCriteria.length() > 0) {
                    filterCriteria.append(',');
                }
                filterCriteria.append("'").append(status).append("'");
            }
            return filterCriteria.toString();
        }
        return null;
    }
    
    /**
     * Uses the map of devices to inventory to output a set of inventoryIds matching the specified deviceIds.
     */
    private Set<Integer> getInventoryFromDevices(Iterable<Integer> devices, Map<Integer, Integer> devicesAndInventory) {
        Set<Integer> inventory = Sets.newHashSet();
        for(Integer deviceId : devices) {
            Integer inventoryId = devicesAndInventory.get(deviceId);
            inventory.add(inventoryId);
        }
        return inventory;
    }
    
    private Duration getCommunicationWindowDuration() {
        return Duration.standardHours(globalSettingDao.getInteger(GlobalSettingType.LAST_COMMUNICATION_HOURS));
    }
    
    private Duration getRuntimeWindowDuration() {
        return Duration.standardHours(globalSettingDao.getInteger(GlobalSettingType.LAST_RUNTIME_HOURS));
    }
}
