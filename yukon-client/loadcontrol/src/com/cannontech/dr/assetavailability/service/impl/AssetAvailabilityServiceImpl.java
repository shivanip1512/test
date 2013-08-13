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
import com.cannontech.common.util.Range;
import com.cannontech.common.util.ReadableRange;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dao.RawPointHistoryDao;
import com.cannontech.core.dao.RawPointHistoryDao.Order;
import com.cannontech.core.dynamic.DynamicDataSource;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.core.dynamic.exception.DynamicDataAccessException;
import com.cannontech.dr.assetavailability.DisplayableApplianceWithRuntime;
import com.cannontech.dr.assetavailability.SimpleAssetAvailability;
import com.cannontech.dr.assetavailability.AssetAvailabilityStatus;
import com.cannontech.dr.assetavailability.DeviceRelayApplianceCategories;
import com.cannontech.dr.assetavailability.SimpleAssetAvailabilitySummary;
import com.cannontech.dr.assetavailability.dao.DRGroupDeviceMappingDao;
import com.cannontech.dr.assetavailability.service.AssetAvailabilityService;
import com.cannontech.loadcontrol.loadgroup.dao.LoadGroupDao;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
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
    @Autowired private CustomerAccountDao customerAccountDao;
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
    public SimpleAssetAvailabilitySummary getAssetAvailabilityFromLoadGroups(Collection<Integer> loadGroupIds) throws DynamicDataAccessException {
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
        Instant communicatingWindowEnd = Instant.now().minus(Duration.standardHours(LAST_COMMUNICATION_HOURS));
        Range<Instant> communicatingWindow = Range.inclusive(communicatingWindowEnd, Instant.now());
        Set<Integer> communicatedInventory = rawPointHistoryDao.getCommunicatingInventoryByLoadGroups(loadGroupIds, communicatingWindow);
        aaSummary.addCommunicating(communicatedInventory);
        
        //Get running
        Multimap<Integer, PaoIdentifier> relayToDeviceIdMultiMap = lmHardwareConfigurationDao.getRelayToDeviceMapByLoadGroups(loadGroupIds);
        Set<PaoIdentifier> allPaosWithRuntime = Sets.newHashSet();
        //do a DB lookup for each of the four runtime attributes
        for(int i = 1; i <= 4; i++) {
            Set<PaoIdentifier> relayPaoIdentifiers = Sets.newHashSet(relayToDeviceIdMultiMap.get(i));
            //remove any that we already found runtime for
            relayPaoIdentifiers = Sets.difference(relayPaoIdentifiers, allPaosWithRuntime);
            
            Instant runtimeWindowEnd = Instant.now().minus(Duration.standardHours(LAST_RUNTIME_HOURS));
            ReadableRange<Instant> runtimeRange = Range.inclusive(runtimeWindowEnd, Instant.now());
            ReadableRange<Long> changeIdRange = Range.unbounded();
            
            Multimap<PaoIdentifier, PointValueQualityHolder> relayAttributeData = rawPointHistoryDao.getAttributeData(relayPaoIdentifiers, RELAY_ATTRIBUTES.get(i), runtimeRange, changeIdRange, false, Order.FORWARD, 0.0);
            
            Set<PaoIdentifier> paosWithRuntime = relayAttributeData.keys().elementSet();
            allPaosWithRuntime.addAll(paosWithRuntime);
            
            //attempt to get remaining paos through dispatch
            relayPaoIdentifiers.removeAll(allPaosWithRuntime);
            Collection<PaoIdentifier> paoIdsWithDispatchRuntime = getPaoRuntimeFromDispatch(relayPaoIdentifiers, RELAY_ATTRIBUTES.get(i));
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
    public Map<Integer, SimpleAssetAvailability> getAssetAvailability(Collection<Integer> inventoryIds) throws DynamicDataAccessException {
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
                buildAssetAvailabilityMap(inventoryIds, optedOutInventory, oneWayInventory, 
                                          inventoryAndDevices, paoCommunicationTimes, paoRelayRuntimes);
        
        return assetAvailabilityMap;
    }
    
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
    
    private Set<PaoIdentifier> getPaoRuntimeFromDispatch(Set<PaoIdentifier> relayPaoIdentifiers, Attribute attribute) {
        Map<Integer, PaoIdentifier> paoIdMap = Maps.newHashMap();
        for(PaoIdentifier paoIdentifier : relayPaoIdentifiers) {
            paoIdMap.put(paoIdentifier.getPaoId(), paoIdentifier);
        }
        //get list of points
        SqlFragmentSource attributeLookupSql = attributeService.getAttributeLookupSql(attribute);
        Map<Integer, Integer> pointsToPao = pointDao.getPointIdsForPaosAndAttribute(attributeLookupSql, paoIdMap.keySet());
        //query dispatch for points w/ value > 0
        Map<Integer, Instant> paoToPointTimeMap = getPaoLatestPointTimes(pointsToPao, 0.0);
        Set<PaoIdentifier> paosWithRuntime = Sets.newHashSet();
        for(Integer paoId : paoToPointTimeMap.keySet()) {
            paosWithRuntime.add(paoIdMap.get(paoId));
        }
        return paosWithRuntime;
    }
    
    private Map<Integer, Instant> getPaoRuntimeTimesFromDispatch(Set<PaoIdentifier> relayPaoIdentifiers, Attribute attribute) {
        Set<Integer> relayDeviceIds = Sets.newHashSet();
        for(PaoIdentifier paoIdentifier : relayPaoIdentifiers) {
            relayDeviceIds.add(paoIdentifier.getPaoId());
        }
        //get list of points
        SqlFragmentSource attributeLookupSql = attributeService.getAttributeLookupSql(attribute);
        Map<Integer, Integer> pointsToPao = pointDao.getPointIdsForPaosAndAttribute(attributeLookupSql, relayDeviceIds);
        //query dispatch for points w/ value > 0
        return getPaoLatestPointTimes(pointsToPao, 0.0);
    }
    
    private Map<Integer, SimpleAssetAvailability> buildAssetAvailabilityMap(Collection<Integer> inventoryIds, 
                                                                       Collection<Integer> optedOutInventory, 
                                                                       Collection<Integer> oneWayInventory, 
                                                                       Map<Integer, Integer> inventoryAndDevices,
                                                                       Map<Integer, Instant> paoCommunicationTimes,
                                                                       PaoRelayRuntimes paoRelayRuntimes) {
        DeviceRelayApplianceCategories dracs = lmHardwareConfigurationDao.getDeviceRelayApplianceCategoryId(inventoryIds);
        Map<Integer, SimpleAssetAvailability> assetAvailabilityMap = Maps.newHashMap();
        Instant now = Instant.now();
        for(int inventoryId : inventoryIds) {
            boolean isOptedOut = optedOutInventory.contains(inventoryId);
            
            if(oneWayInventory.contains(inventoryId)) {
                SimpleAssetAvailability assetAvailability = new SimpleAssetAvailability(inventoryId, isOptedOut);
                assetAvailabilityMap.put(inventoryId, assetAvailability);
            } else {
                int deviceId = inventoryAndDevices.get(inventoryId);
                Instant lastCommunicationTime = paoCommunicationTimes.get(deviceId);
                //get runtime
                Map<Integer, Instant> relayRuntimes = paoRelayRuntimes.get(deviceId);
                Set<DisplayableApplianceWithRuntime> applianceRuntimes = Sets.newHashSet();
                if(relayRuntimes != null) {
                    for(Map.Entry<Integer, Instant> relayRuntime : relayRuntimes.entrySet()) {
                        int relay = relayRuntime.getKey();
                        Instant runtime = relayRuntime.getValue();
                        int applianceCategoryId = dracs.getApplianceCategoryId(deviceId, relay);
                        applianceRuntimes.add(new DisplayableApplianceWithRuntime(applianceCategoryId, runtime));
                    }
                }
                //get status
                AssetAvailabilityStatus status = getStatus(now, lastCommunicationTime, applianceRuntimes);
                
                SimpleAssetAvailability assetAvailability = new SimpleAssetAvailability(inventoryId, status, isOptedOut, 
                                                               lastCommunicationTime, applianceRuntimes);
                assetAvailabilityMap.put(inventoryId, assetAvailability);
            }
        }
        return assetAvailabilityMap;
    }
    
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
    
    private Set<Integer> getTwoWayInventoryDevices(Map<Integer, Integer> inventoryAndDevices) {
        Set<Integer> deviceIds = Sets.newHashSet(inventoryAndDevices.values());
        deviceIds.remove(0); //Remove system device (possibly in this list due to 1-way inventory)
        return deviceIds;
    }
    
    private AssetAvailabilityStatus getStatus(Instant now, Instant lastCommunicationTime, 
                                              Collection<DisplayableApplianceWithRuntime> applianceRuntimes) {
        boolean communicating = false;
        boolean hasRuntime = false;
        Instant endOfCommunicationWindow = now.minus(Duration.standardHours(LAST_COMMUNICATION_HOURS));
        if(lastCommunicationTime != null && lastCommunicationTime.isAfter(endOfCommunicationWindow)) {
            communicating = true;
        }
        Instant endOfRuntimeWindow = now.minus(Duration.standardHours(LAST_RUNTIME_HOURS));
        for(DisplayableApplianceWithRuntime runtime : applianceRuntimes) {
            if(runtime.getLastNonZeroRuntime().isAfter(endOfRuntimeWindow)) {
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
    
    //gets current values of all specified points, but ignores any whose value is less than valueLowerBound
    private Map<Integer, Instant> getPaoLatestPointTimes(Map<Integer, Integer> pointsToPaos, Double valueLowerBound) {
        Map<Integer, Instant> paoCommunicationTimes = Maps.newHashMap();
        Set<Integer> pointIds = pointsToPaos.keySet();
        
        //get point values from dispatch
        Set<? extends PointValueQualityHolder> pointValues = dynamicDataSource.getPointValue(pointIds);
        //pick out most recent for each pao
        for(PointValueQualityHolder pointValue : pointValues) {
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
    
    private Set<Integer> getInventoryIdsFromPaoIdentifiers(Map<Integer, Integer> inventoryAndDevices, Set<PaoIdentifier> paoIds) {
        Map<Integer, Integer> devicesAndInventory = HashBiMap.create(inventoryAndDevices).inverse();
        Set<Integer> inventory = Sets.newHashSet();
        for(PaoIdentifier paoId : paoIds) {
            inventory.add(devicesAndInventory.get(paoId.getPaoId()));
        }
        return inventory;
    }
    
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
