package com.cannontech.dr.assetavailability.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.util.RecentResultsCache;
import com.cannontech.dr.assetavailability.dao.DRGroupDeviceMappingDao;
import com.cannontech.dr.assetavailability.ping.AssetAvailabilityPingStrategy;
import com.cannontech.dr.assetavailability.ping.AssetAvailabilityReadResult;
import com.cannontech.dr.assetavailability.service.AssetAvailabilityPingService;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class AssetAvailabilityPingServiceImpl implements AssetAvailabilityPingService {
    @Autowired @Qualifier("assetAvailabilityReads") private RecentResultsCache<AssetAvailabilityReadResult> recentResultsCache;
    @Autowired private DRGroupDeviceMappingDao drGroupDeviceMappingDao;
    @Autowired private List<AssetAvailabilityPingStrategy> pingStrategies;
    private Map<Integer, String> paoIdToResultIdMap = Maps.newConcurrentMap();
    
    public void readDevicesInDrGrouping(PaoIdentifier paoIdentifier) {
        //Get devices
        Set<YukonPao> devices = drGroupDeviceMappingDao.getDevicesForGrouping(paoIdentifier);
        
        //Split devices by strategy
        Set<YukonPao> validDevices = Sets.newHashSet();
        Map<AssetAvailabilityPingStrategy, Set<? extends YukonPao>> strategyDeviceMap = Maps.newHashMap();
        for(AssetAvailabilityPingStrategy strategy : pingStrategies) {
            Set<YukonPao> strategyDevices = strategy.filterDevices(devices);
            if(strategyDevices.size() > 0) {
                strategyDeviceMap.put(strategy, strategyDevices);
                validDevices.addAll(strategyDevices);
            }
        }
        //invalid devices are those with no valid strategy
        Set<YukonPao> invalidDevices = Sets.difference(devices, validDevices);
        Set<Integer> validDeviceIds = Sets.newHashSet(Iterables.transform(validDevices, paoToIdFunction));
        Set<Integer> invalidDeviceIds = Sets.newHashSet(Iterables.transform(invalidDevices, paoToIdFunction));
        
        //Set up results
        AssetAvailabilityReadResult result = new AssetAvailabilityReadResult(validDeviceIds, invalidDeviceIds);
        String resultId = recentResultsCache.addResult(result);
        paoIdToResultIdMap.put(paoIdentifier.getPaoId(), resultId);
        
        //Perform reads using multiple strategies
        for(Map.Entry<AssetAvailabilityPingStrategy, Set<? extends YukonPao>> entry : strategyDeviceMap.entrySet()) {
            AssetAvailabilityPingStrategy strategy = entry.getKey();
            Set<? extends YukonPao> strategyDevices = entry.getValue();
            strategy.readDevices(strategyDevices, resultId);
        }
    }
    
    public AssetAvailabilityReadResult getReadResult(int paoId) {
        String resultId = paoIdToResultIdMap.get(paoId);
        if(resultId == null) {
            return null;
        }
        return recentResultsCache.getResult(resultId);
    }
    
    public boolean hasReadResult(int paoId) {
        String resultId = paoIdToResultIdMap.get(paoId);
        if(resultId == null) {
            return false;
        } else {
            AssetAvailabilityReadResult result = recentResultsCache.getResult(resultId);
            if(result == null || result.isComplete()) {
                paoIdToResultIdMap.remove(paoId); //remove old mapping
                return false;
            }
            return true;
        }
    }
    
    //Function to pull paoId from a YukonPao
    private static final Function<YukonPao, Integer> paoToIdFunction = new Function<YukonPao, Integer>() {
        public Integer apply(YukonPao pao) {
            return pao.getPaoIdentifier().getPaoId();
        }
    };
}
