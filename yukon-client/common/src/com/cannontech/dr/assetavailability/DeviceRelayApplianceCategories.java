package com.cannontech.dr.assetavailability;

import java.util.Collection;
import java.util.Map;

import com.google.common.collect.Maps;

public class DeviceRelayApplianceCategories {
    Map<Integer, Map<Integer, Integer>> map = Maps.newHashMap();
    
    public DeviceRelayApplianceCategories(Collection<DeviceRelayApplianceCategory> dracs) {
        for(DeviceRelayApplianceCategory drac : dracs) {
            add(drac);
        }
    }
    
    public void add(DeviceRelayApplianceCategory drac) {
        if(map.containsKey(drac.getDeviceId())) {
            map.get(drac.getDeviceId()).put(drac.getRelay(), drac.getApplianceCategoryId());
        } else {
            Map<Integer, Integer> relayMap = Maps.newHashMap();
            relayMap.put(drac.getRelay(), drac.getApplianceCategoryId());
            map.put(drac.getDeviceId(), relayMap);
        }
    }
    
    public int getApplianceCategoryId(int deviceId, int relay) {
        return map.get(deviceId).get(relay);
    }
}
