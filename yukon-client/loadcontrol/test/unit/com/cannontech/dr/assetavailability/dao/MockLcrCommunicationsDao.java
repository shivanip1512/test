package com.cannontech.dr.assetavailability.dao;

import java.util.Collection;
import java.util.Map;

import com.cannontech.dr.assetavailability.AllRelayCommunicationTimes;
import com.cannontech.dr.assetavailability.AssetAvailabilityPointDataTimes;
import com.cannontech.dr.assetavailability.DeviceCommunicationTimes;
import com.google.common.collect.Maps;

public class MockLcrCommunicationsDao implements DynamicLcrCommunicationsDao {
    private Map<Integer, AssetAvailabilityPointDataTimes> data = Maps.newHashMap();
    
    public MockLcrCommunicationsDao() {}
    
    public MockLcrCommunicationsDao(Map<Integer, AssetAvailabilityPointDataTimes> data) {
        this.data = data;
    }
    
    @Override
    public Map<Integer, DeviceCommunicationTimes> findTimes(Collection<Integer> deviceIds) {
        Map<Integer, DeviceCommunicationTimes> map = Maps.newHashMap();
        for (int deviceId : deviceIds) {
            map.put(deviceId, getAllTimes(deviceId));
        }
        return map;
    }

    @Override
    public Map<Integer, AllRelayCommunicationTimes> findAllRelayCommunicationTimes(Collection<Integer> deviceIds) {
        Map<Integer, AllRelayCommunicationTimes> map = Maps.newHashMap();
        for (int deviceId : deviceIds) {
            map.put(deviceId, getAllTimes(deviceId));
        }
        return map;
    }

    @Override
    public void insertData(AssetAvailabilityPointDataTimes times) {
        data.put(times.getDeviceId(), times);
    }
    
    private AllRelayCommunicationTimes getAllTimes(int deviceId) {
        AssetAvailabilityPointDataTimes times = data.get(deviceId);
        if(times == null) {
            return new AllRelayCommunicationTimes(null, null, null, null, null, null);
        }
        return new AllRelayCommunicationTimes(times.getLastCommunicationTime(), times.getLastNonZeroRuntime(), 
                                              times.getRelayRuntime(1), times.getRelayRuntime(2), 
                                              times.getRelayRuntime(3), times.getRelayRuntime(4));
        
    }
    
}
