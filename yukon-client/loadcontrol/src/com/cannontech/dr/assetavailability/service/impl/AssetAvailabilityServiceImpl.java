package com.cannontech.dr.assetavailability.service.impl;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.joda.time.Duration;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.point.PointQuality;
import com.cannontech.common.util.Range;
import com.cannontech.common.util.ReadableRange;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dao.RawPointHistoryDao;
import com.cannontech.core.dao.RawPointHistoryDao.Order;
import com.cannontech.core.dynamic.DynamicDataSource;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.core.dynamic.exception.DynamicDataAccessException;
import com.cannontech.dr.assetavailability.ApplianceAssetAvailabilitySummary;
import com.cannontech.dr.assetavailability.ApplianceWithRuntime;
import com.cannontech.dr.assetavailability.InventoryRelayAppliances;
import com.cannontech.dr.assetavailability.SimpleAssetAvailability;
import com.cannontech.dr.assetavailability.AssetAvailabilityStatus;
import com.cannontech.dr.assetavailability.DeviceRelayApplianceCategories;
import com.cannontech.dr.assetavailability.SimpleAssetAvailabilitySummary;
import com.cannontech.dr.assetavailability.dao.DRGroupDeviceMappingDao;
import com.cannontech.dr.assetavailability.service.AssetAvailabilityService;
import com.cannontech.loadcontrol.loadgroup.dao.LoadGroupDao;
import com.cannontech.stars.dr.appliance.dao.ApplianceDao;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.dr.hardware.dao.LMHardwareConfigurationDao;
import com.cannontech.stars.dr.optout.dao.OptOutEventDao;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

public class AssetAvailabilityServiceImpl implements AssetAvailabilityService {
    @Autowired private OptOutEventDao optOutEventDao;
    @Autowired private RawPointHistoryDao rawPointHistoryDao;
    @Autowired private DeviceDao deviceDao;
    @Autowired private LMHardwareConfigurationDao lmHardwareConfigurationDao;
    @Autowired private ApplianceDao applianceDao;
    @Autowired private InventoryDao inventoryDao;
    @Autowired private LoadGroupDao loadGroupDao;
    @Autowired private DRGroupDeviceMappingDao drGroupDeviceMappingDao;
    @Autowired private DynamicDataSource dynamicDataSource;
    @Autowired private PointDao pointDao;
    @Autowired private AttributeService attributeService;
    
    private static final int LAST_COMMUNICATION_HOURS = 60;
    private static final int LAST_RUNTIME_HOURS = 168;
    private static final ImmutableMap<Integer, ? extends Attribute> RELAY_ATTRIBUTES =
            ImmutableMap.of(1, BuiltInAttribute.RELAY_1_RUN_TIME_DATA_LOG,
                2, BuiltInAttribute.RELAY_2_RUN_TIME_DATA_LOG,
                3, BuiltInAttribute.RELAY_3_RUN_TIME_DATA_LOG,
                4, BuiltInAttribute.RELAY_4_RUN_TIME_DATA_LOG);
    
    @Override
    public SimpleAssetAvailabilitySummary getAssetAvailabilityFromDrGroup(PaoIdentifier drPaoIdentifier) throws DynamicDataAccessException {
        Set<Integer> loadGroupIds = drGroupDeviceMappingDao.getLoadGroupIdsForDrGroup(drPaoIdentifier);
        return getAssetAvailabilityFromLoadGroups(loadGroupIds);
    }
    
    @Override
    public ApplianceAssetAvailabilitySummary getApplianceAssetAvailability(PaoIdentifier drPaoIdentifier) throws DynamicDataAccessException {
        Set<Integer> loadGroupIds = drGroupDeviceMappingDao.getLoadGroupIdsForDrGroup(drPaoIdentifier);
        return getApplianceAssetAvailability(loadGroupIds);
    }
    
    @Override
    public ApplianceAssetAvailabilitySummary getApplianceAssetAvailability(Iterable<Integer> loadGroupIds) throws DynamicDataAccessException {
        //Get all inventory & associated paos
        Map<Integer, Integer> inventoryAndDevices = drGroupDeviceMappingDao.getInventoryAndDeviceIdsForLoadGroups(loadGroupIds);
        Set<Integer> inventoryIds = Sets.newHashSet(inventoryAndDevices.keySet());
        //Get appliances
        InventoryRelayAppliances inventoryRelayAppliances = lmHardwareConfigurationDao.getInventoryRelayAppliances(inventoryIds);
        Multimap<Integer, Integer> inventoryToApplianceMultimap = inventoryRelayAppliances.getInventoryToApplianceMultimap();
        Set<Integer> allApplianceIds = Sets.newHashSet(inventoryToApplianceMultimap.values());
        ApplianceAssetAvailabilitySummary summary = new ApplianceAssetAvailabilitySummary(allApplianceIds);
        
        //1-way inventory are always considered communicating and running
        Set<Integer> oneWayInventory = Sets.newHashSet();
        for(Map.Entry<Integer, Integer> pair : inventoryAndDevices.entrySet()) {
            //1-way inventory are mapped to device id 0
            if(pair.getValue() == 0) {
                oneWayInventory.add(pair.getKey());
            }
        }
        Set<Integer> oneWayAppliances = getAppliancesFromInventory(inventoryToApplianceMultimap, oneWayInventory);
        summary.addCommunicating(oneWayAppliances);
        summary.addRunning(oneWayAppliances);
        
        //Get opted out
        Set<Integer> optedOutInventory = optOutEventDao.getOptedOutInventoryByLoadGroups(loadGroupIds);
        Set<Integer> optedOutAppliances = getAppliancesFromInventory(inventoryToApplianceMultimap, optedOutInventory);
        summary.addOptedOut(optedOutAppliances);
        
        //Get communicating
        Instant now = Instant.now();
        Instant communicatingWindowEnd = now.minus(Duration.standardHours(LAST_COMMUNICATION_HOURS));
        Range<Instant> communicatingWindow = Range.inclusive(communicatingWindowEnd, now);
        Set<Integer> communicatedInventory = rawPointHistoryDao.getCommunicatingInventoryByLoadGroups(loadGroupIds, communicatingWindow);
        Set<Integer> communicatedAppliances = getAppliancesFromInventory(inventoryToApplianceMultimap, communicatedInventory);
        summary.addCommunicating(communicatedAppliances);
        
        //Get running
        Multimap<Integer, PaoIdentifier> relayToDeviceIdMultiMap = lmHardwareConfigurationDao.getRelayToDeviceMapByLoadGroups(loadGroupIds);
        //do a DB lookup for each of the four runtime attributes
        for(int relay = 1; relay <= 4; relay++) {
            Set<PaoIdentifier> relayPaoIdentifiers = Sets.newHashSet(relayToDeviceIdMultiMap.get(relay));
            
            Instant runtimeWindowEnd = Instant.now().minus(Duration.standardHours(LAST_RUNTIME_HOURS));
            Range<Instant> runtimeRange = Range.inclusive(runtimeWindowEnd, Instant.now());
            Range<Long> changeIdRange = Range.unbounded();
            
            Multimap<PaoIdentifier, PointValueQualityHolder> relayAttributeData = rawPointHistoryDao.getAttributeData(relayPaoIdentifiers, RELAY_ATTRIBUTES.get(relay), runtimeRange, changeIdRange, false, Order.FORWARD, 0.0);
            
            Set<PaoIdentifier> paosWithRuntime = Sets.newHashSet(relayAttributeData.keys().elementSet());
            
            //attempt to get remaining paos through dispatch
            relayPaoIdentifiers.removeAll(paosWithRuntime);
            Collection<PaoIdentifier> paosWithDispatchRuntime = getPaoRuntimeFromDispatch(relayPaoIdentifiers, RELAY_ATTRIBUTES.get(relay));
            paosWithRuntime.addAll(paosWithDispatchRuntime);
            
            Set<Integer> inventoryWithRuntime = getInventoryIdsFromPaoIdentifiers(inventoryAndDevices, paosWithRuntime);
            Set<Integer> appliancesWithRuntime = getApplianceIdsFromPaoIdentifiersAndRelay(inventoryRelayAppliances, inventoryWithRuntime, relay);
            summary.addRunning(appliancesWithRuntime);    
        }
        
        return summary;
    }
    
    @Override
    public SimpleAssetAvailabilitySummary getAssetAvailabilityFromLoadGroups(Iterable<Integer> loadGroupIds) throws DynamicDataAccessException {
        //Get all inventory & associated paos
        Map<Integer, Integer> inventoryAndDevices = drGroupDeviceMappingDao.getInventoryAndDeviceIdsForLoadGroups(loadGroupIds);
        Set<Integer> inventoryIds = Sets.newHashSet(inventoryAndDevices.keySet());
        SimpleAssetAvailabilitySummary aaSummary = new SimpleAssetAvailabilitySummary(inventoryIds);
        
        //1-way inventory are always considered communicating and running
        Set<Integer> oneWayInventory = Sets.newHashSet();
        for(Map.Entry<Integer, Integer> pair : inventoryAndDevices.entrySet()) {
            //1-way inventory are mapped to device id 0
            if(pair.getValue() == 0) {
                oneWayInventory.add(pair.getKey());
            }
        }
        aaSummary.addCommunicating(oneWayInventory);
        aaSummary.addRunning(oneWayInventory);
        
        //Get opted out
        Set<Integer> optedOutInventory = optOutEventDao.getOptedOutInventoryByLoadGroups(loadGroupIds);
        aaSummary.addOptedOut(optedOutInventory);
        
        //Get communicating
        Instant now = Instant.now();
        Instant communicatingWindowEnd = now.minus(Duration.standardHours(LAST_COMMUNICATION_HOURS));
        Range<Instant> communicatingWindow = Range.inclusive(communicatingWindowEnd, now);
        Set<Integer> communicatedInventory = rawPointHistoryDao.getCommunicatingInventoryByLoadGroups(loadGroupIds, communicatingWindow);
        aaSummary.addCommunicating(communicatedInventory);
        
        //Get running
        Multimap<Integer, PaoIdentifier> relayToDeviceIdMultiMap = lmHardwareConfigurationDao.getRelayToDeviceMapByLoadGroups(loadGroupIds);
        Set<PaoIdentifier> allPaosWithRuntime = Sets.newHashSet();
        //do a DB lookup for each of the four runtime attributes
        for(int relay = 1; relay <= 4; relay++) {
            Set<PaoIdentifier> relayPaoIdentifiers = Sets.newHashSet(relayToDeviceIdMultiMap.get(relay));
            //remove any that we already found runtime for
            relayPaoIdentifiers = Sets.difference(relayPaoIdentifiers, allPaosWithRuntime);
            
            Instant runtimeWindowEnd = Instant.now().minus(Duration.standardHours(LAST_RUNTIME_HOURS));
            Range<Instant> runtimeRange = Range.inclusive(runtimeWindowEnd, Instant.now());
            Range<Long> changeIdRange = Range.unbounded();
            
            Multimap<PaoIdentifier, PointValueQualityHolder> relayAttributeData = rawPointHistoryDao.getAttributeData(relayPaoIdentifiers, RELAY_ATTRIBUTES.get(relay), runtimeRange, changeIdRange, false, Order.FORWARD, 0.0);
            
            Set<PaoIdentifier> paosWithRuntime = relayAttributeData.keys().elementSet();
            allPaosWithRuntime.addAll(paosWithRuntime);
            
            //attempt to get remaining paos through dispatch
            relayPaoIdentifiers.removeAll(allPaosWithRuntime);
            Collection<PaoIdentifier> paoIdsWithDispatchRuntime = getPaoRuntimeFromDispatch(relayPaoIdentifiers, RELAY_ATTRIBUTES.get(relay));
            allPaosWithRuntime.addAll(paoIdsWithDispatchRuntime);
            
            Set<Integer> inventoryWithRuntime = getInventoryIdsFromPaoIdentifiers(inventoryAndDevices, paosWithRuntime);
            aaSummary.addRunning(inventoryWithRuntime);    
        }
        
        return aaSummary;
    }
    
    @Override
    public SimpleAssetAvailability getAssetAvailability(int inventoryIds) throws DynamicDataAccessException {
        Map<Integer, SimpleAssetAvailability> singleValueMap = getAssetAvailability(Sets.newHashSet(inventoryIds));
        return singleValueMap.get(inventoryIds);
    }
    
    @Override
    public Map<Integer, SimpleAssetAvailability> getAssetAvailability(Iterable<Integer> inventoryIds) throws DynamicDataAccessException {
        Map<Integer, Integer> inventoryAndDevices = inventoryDao.getDeviceIds(inventoryIds);
        Set<Integer> oneWayInventory = getOneWayInventory(inventoryAndDevices);
        Set<Integer> deviceIds = getTwoWayInventoryDevices(inventoryAndDevices);
        
        //Get opted out inventory
        Set<Integer> optedOutInventory = optOutEventDao.getOptedOutInventory(inventoryIds);
        
        //Get latest communication time from dispatch
        Map<Integer, Integer> pointsToPaos = pointDao.getPointIdsForPaos(deviceIds);
        Map<Integer, Instant> paoCommunicationTimes = getPaoLatestPointTimes(pointsToPaos, null);
        
        //Get latest non-zero runtime reported
        PaoRelayRuntimes paoRelayRuntimes = new PaoRelayRuntimes();
        Multimap<Integer, PaoIdentifier> relayToDeviceIdMultiMap = lmHardwareConfigurationDao.getRelayToDeviceMapByDeviceIds(deviceIds);
        for(int relay = 1; relay <= 4; relay++) {
            Attribute relayAttribute = RELAY_ATTRIBUTES.get(relay);
            Set<PaoIdentifier> relayPaoIdentifiers = Sets.newHashSet(relayToDeviceIdMultiMap.get(relay));
            Map<Integer, Instant> paoRuntimeTimes = getPaoRuntimeTimesFromDispatch(relayPaoIdentifiers, relayAttribute);
            
            for(Map.Entry<Integer, Instant> entry : paoRuntimeTimes.entrySet()) {
                paoRelayRuntimes.put(entry.getKey(), relay, entry.getValue());
            }
            
            //remove paos from the list that just got non-zero runtime from dispatch
            for(Iterator<PaoIdentifier> iterator = relayPaoIdentifiers.iterator(); iterator.hasNext();) {
                PaoIdentifier relayPaoIdentifier = iterator.next();
                if(paoRuntimeTimes.containsKey(relayPaoIdentifier.getPaoId())) {
                    iterator.remove();
                }
            }
            
            //query database for remainder
            ReadableRange<Instant> runtimeRange = Range.unbounded();
            ReadableRange<Long> changeIdRange = Range.unbounded();
            Multimap<PaoIdentifier, PointValueQualityHolder> relayAttributeData = 
                    rawPointHistoryDao.getAttributeData(relayPaoIdentifiers, relayAttribute, 
                                                        runtimeRange, changeIdRange, false, 
                                                        Order.FORWARD, 0.0);
            
            //add successful results to the map of device -> latest runtime
            updatePaoRuntimeTimesFromDatabase(paoRelayRuntimes, relayAttributeData, relay);
        }
        
        //build the asset availability objects
        Map<Integer, SimpleAssetAvailability> assetAvailabilityMap = 
                buildAssetAvailabilityMap(inventoryIds, optedOutInventory, oneWayInventory, inventoryAndDevices, 
                                          paoCommunicationTimes, paoRelayRuntimes);
        
        return assetAvailabilityMap;
    }
    
    @Override
    public Map<Integer, SimpleAssetAvailability> getAssetAvailability(PaoIdentifier paoIdentifier) {
        Set<Integer> loadGroupIds = drGroupDeviceMappingDao.getLoadGroupIdsForDrGroup(paoIdentifier);
        Set<Integer> inventoryIds = drGroupDeviceMappingDao.getInventoryAndDeviceIdsForLoadGroups(loadGroupIds).keySet();
        return getAssetAvailability(inventoryIds);
    }
    
    /*
     * Each relayAttributeData entry is checked against the paoRelayRuntimes - if there is no entry for that pao/relay
     * in paoRelayRuntimes, the relayAttributeData entry is added. If there is existing data, the entry is only added if
     * it has a later timestamp.
     * The end result is that paoRelayRuntimes is updated to contain any newer data from relayAttributeData.
     */
    private void updatePaoRuntimeTimesFromDatabase(PaoRelayRuntimes paoRelayRuntimes, Multimap<PaoIdentifier, 
                                                PointValueQualityHolder> relayAttributeData, int relay) {
        for(Map.Entry<PaoIdentifier, PointValueQualityHolder> entry : relayAttributeData.entries()) {
            //runtime is not 0 and (previous runtimes doesn't contain pao || data is newer)
            int paoId = entry.getKey().getPaoId();
            Double pointValue = entry.getValue().getValue();
            Instant pointTimestamp = new Instant(entry.getValue().getPointDataTimeStamp());
            if(pointValue > 0 && 
                    (!paoRelayRuntimes.containsPao(paoId) || 
                     pointTimestamp.isAfter(paoRelayRuntimes.getRuntime(paoId, relay)))) {
                paoRelayRuntimes.put(paoId, relay, pointTimestamp);
            }
        }
    }
    
    /*
     * Queries dispatch for point data with a value greater than 0, for the specified attribute, on the specified paos.
     * Returns the subset of paoIdentifiers that returned valid values. (Note that any value greater than 0, at any
     * time, is considered valid to this method.)
     */
    private Set<PaoIdentifier> getPaoRuntimeFromDispatch(Set<PaoIdentifier> relayPaoIdentifiers, Attribute attribute) {
        Map<Integer, PaoIdentifier> paoIdMap = Maps.newHashMap();
        for(PaoIdentifier paoIdentifier : relayPaoIdentifiers) {
            paoIdMap.put(paoIdentifier.getPaoId(), paoIdentifier);
        }
        //get list of points
        Map<Integer, Integer> pointsToPao = pointDao.getPointIdsForPaosAndAttribute(attribute, paoIdMap.keySet());
        //query dispatch for points w/ value > 0
        Map<Integer, Instant> paoToPointTimeMap = getPaoLatestPointTimes(pointsToPao, 0.0);
        Set<PaoIdentifier> paosWithRuntime = Sets.newHashSet();
        for(Integer paoId : paoToPointTimeMap.keySet()) {
            paosWithRuntime.add(paoIdMap.get(paoId));
        }
        return paosWithRuntime;
    }
    
    /*
     * Queries dispatch for point data with a value greater than 0, for the specified attribute, on the specified paos.
     * Returns a map of deviceId to instant of last non-zero read.
     */
    private Map<Integer, Instant> getPaoRuntimeTimesFromDispatch(Set<PaoIdentifier> relayPaoIdentifiers, Attribute attribute) {
        Set<Integer> relayDeviceIds = Sets.newHashSet();
        for(PaoIdentifier paoIdentifier : relayPaoIdentifiers) {
            relayDeviceIds.add(paoIdentifier.getPaoId());
        }
        //get list of points
        Map<Integer, Integer> pointsToPao = pointDao.getPointIdsForPaosAndAttribute(attribute, relayDeviceIds);
        //query dispatch for points w/ value > 0
        return getPaoLatestPointTimes(pointsToPao, 0.0);
    }
    
    /*
     * From the supplied sets and maps, this method determines the asset availability of each inventory and returns a
     * map of inventoryId to SimpleAssetAvailability of that inventory.
     * One-way inventory are considered to be always communicating and running, but the communication time and runtimes
     * on their SimpleAssetAvailability object will be null.
     * Two-way inventory may also have null communication time and/or an empty set of runtimes if no communication or
     * runtime data has ever been collected for the device.
     */
    private Map<Integer, SimpleAssetAvailability> buildAssetAvailabilityMap(Iterable<Integer> inventoryIds, 
                                                                       Collection<Integer> optedOutInventory, 
                                                                       Collection<Integer> oneWayInventory, 
                                                                       Map<Integer, Integer> inventoryAndDevices,
                                                                       Map<Integer, Instant> paoCommunicationTimes,
                                                                       PaoRelayRuntimes paoRelayRuntimes) {
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
                Instant lastCommunicationTime = paoCommunicationTimes.get(deviceId);
                Map<Integer, Integer> relayApplianceMap = inventoryRelayAppliances.getRelayApplianceMap(inventoryId);
                Map<Integer, Instant> relayRuntimes = paoRelayRuntimes.get(deviceId);
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
    
    /*
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
    
    /*
     * Given the map of inventoryIds to deviceIds, this method returns the subset of inventoryIds that belong to
     * two-way inventory. (This is done by removing all inventory that map to deviceId of 0, which are one-way
     * inventory, since they don't map to a real pao.)
     */
    private Set<Integer> getTwoWayInventoryDevices(Map<Integer, Integer> inventoryAndDevices) {
        Set<Integer> deviceIds = Sets.newHashSet(inventoryAndDevices.values());
        deviceIds.remove(0); //Remove system device (possibly in this list due to 1-way inventory)
        return deviceIds;
    }
    
    /*
     * Gets an AssetAvailabilityStatus by comparing the current instant to the instant of last communication time and
     * the instants of last runtime(s). The device is communicating if the last communication time is within
     * LAST_COMMUNICATION_HOURS of the current time, and the device is running if any of the non-zero runtime values
     * was reported within LAST_RUNTIME_HOURS of the current time.
     */
    private AssetAvailabilityStatus getStatus(Instant now, Instant lastCommunicationTime, 
                                              Collection<ApplianceWithRuntime> applianceRuntimes) {
        boolean communicating = false;
        boolean hasRuntime = false;
        Instant endOfCommunicationWindow = now.minus(Duration.standardHours(LAST_COMMUNICATION_HOURS));
        if(lastCommunicationTime != null && lastCommunicationTime.isAfter(endOfCommunicationWindow)) {
            communicating = true;
        }
        Instant endOfRuntimeWindow = now.minus(Duration.standardHours(LAST_RUNTIME_HOURS));
        for(ApplianceWithRuntime applianceRuntime : applianceRuntimes) {
            Instant runtime = applianceRuntime.getLastNonZeroRuntime();
            if(runtime != null && runtime.isAfter(endOfRuntimeWindow)) {
                hasRuntime = true;
                break;
            }
        }
        if(communicating) {
            if(hasRuntime) {
                return AssetAvailabilityStatus.IN_COMMUNICATION_RUNNING;
            }
            return AssetAvailabilityStatus.IN_COMMUNICATION_NOT_RUNNING;
        }
        return AssetAvailabilityStatus.UNAVAILABLE;
    }
    
    /*
     * Gets current values of all specified points from dispatch, but ignores any whose value is less than 
     * valueLowerBound.
     */
    private Map<Integer, Instant> getPaoLatestPointTimes(Map<Integer, Integer> pointsToPaos, Double valueLowerBound) {
        Map<Integer, Instant> paoCommunicationTimes = Maps.newHashMap();
        Set<Integer> pointIds = pointsToPaos.keySet();
        
        //get point values from dispatch
        Set<? extends PointValueQualityHolder> pointValues = dynamicDataSource.getPointValue(pointIds);
        //pick out most recent for each pao
        for(PointValueQualityHolder pointValue : pointValues) {
            if(pointValue.getPointQuality() != PointQuality.Normal) continue;
            int paoId = pointsToPaos.get(pointValue.getId());
            Instant newTimestamp = new Instant(pointValue.getPointDataTimeStamp());
            Instant oldTimestamp = paoCommunicationTimes.get(paoId);
            if(oldTimestamp == null || oldTimestamp.isBefore(newTimestamp)) {
                if (valueLowerBound == null || pointValue.getValue() > valueLowerBound) {
                    paoCommunicationTimes.put(paoId, newTimestamp);
                }
            }
        }
        
        return paoCommunicationTimes;
    }
    
    /*
     * Takes a map of inventoryIds to deviceIds and a set of paoIdentifiers. Uses the map to find inventoryId matching
     * the deviceId for each paoIdentifier (if one exists). Returns the set of inventoryIds. 
     */
    private Set<Integer> getInventoryIdsFromPaoIdentifiers(Map<Integer, Integer> inventoryAndDevices, Set<PaoIdentifier> paoIds) {
        Map<Integer, Integer> devicesAndInventory = HashBiMap.create(inventoryAndDevices).inverse();
        Set<Integer> inventory = Sets.newHashSet();
        for(PaoIdentifier paoId : paoIds) {
            Integer inventoryId = devicesAndInventory.get(paoId.getPaoId());
            if(inventoryId != null) {
                inventory.add(inventoryId);
            }
        }
        return inventory;
    }
    
    /*
     * Uses the InventoryToApplianceMultimap to get the set of all appliances attached to the specified inventory.
     */
    private Set<Integer> getAppliancesFromInventory(Multimap<Integer, Integer> inventoryToApplianceMultimap, Iterable<Integer> inventoryIds) {
        Set<Integer> applianceIds = Sets.newHashSet();
        for(int inventoryId : inventoryIds) {
            applianceIds.addAll(inventoryToApplianceMultimap.get(inventoryId));
        }
        return applianceIds;
    }
    
    /*
     * Uses the InventoryRelayAppliances to get the appliances on the specified relay for all specified paos.
     */
    private Set<Integer> getApplianceIdsFromPaoIdentifiersAndRelay(InventoryRelayAppliances inventoryRelayAppliances, 
                                                                   Set<Integer> inventoryWithRuntime, int relay) {
        Set<Integer> appliances = Sets.newHashSet();
        for(Integer inventoryId : inventoryWithRuntime) {
            int applianceId = inventoryRelayAppliances.getApplianceId(inventoryId, relay);
            appliances.add(applianceId);
        }
        return appliances;
    }
    
    /*
     * A simple wrapper around a map of a map. This is mostly just to ease readability.
     */
    private static class PaoRelayRuntimes {
        Map<Integer, Map<Integer, Instant>> map = Maps.newHashMap();
        
        public void put(int paoId, int relay, Instant runtime) {
            Map<Integer, Instant> relayToRuntime = Maps.newHashMap();
            relayToRuntime.put(relay, runtime);
            map.put(paoId, relayToRuntime);
        }
        
        public Map<Integer, Instant> get(int paoId) {
            return map.get(paoId);
        }
        
        public boolean containsPao(int paoId) {
            return map.containsKey(paoId);
        }
        
        public Instant getRuntime(int paoId, int relay) {
            return map.get(paoId).get(relay);
        }
    }
}
