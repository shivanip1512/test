package com.cannontech.dr.assetavailability;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

public final class InventoryRelayAppliances {
    private final Map<Integer, Map<Integer, Integer>> map = Maps.newHashMap();
    private final Map<Integer, Integer> applianceToCategoryMap = Maps.newHashMap();
    
    public InventoryRelayAppliances(Iterable<InventoryRelayAppliance> iras) {
        for (InventoryRelayAppliance ira : iras) {
            add(ira);
        }
    }
    
    public InventoryRelayAppliances() {}
    
    public void add(int inventoryId, int relay, int applianceId, int applianceCategoryId) {
        if (map.containsKey(inventoryId)) {
            map.get(inventoryId).put(relay, applianceId);
        } else {
            Map<Integer, Integer> relayMap = new HashMap<>();
            relayMap.put(relay, applianceId);
            map.put(inventoryId, relayMap);
        }
        applianceToCategoryMap.put(applianceId, applianceCategoryId);
    }
    
    public void add(InventoryRelayAppliance ira) {
        if (map.containsKey(ira.getInventoryId())) {
            map.get(ira.getInventoryId()).put(ira.getRelay(), ira.getApplianceId());
        } else {
            Map<Integer, Integer> relayMap = new HashMap<>();
            relayMap.put(ira.getRelay(), ira.getApplianceId());
            map.put(ira.getInventoryId(), relayMap);
        }
        applianceToCategoryMap.put(ira.getApplianceId(), ira.getApplianceCategoryId());
    }
    
    public Integer getApplianceId(int inventoryId, int relay) {
        return map.get(inventoryId).get(relay);
    }
    
    public Integer getApplianceCategoryId(int inventoryId, int relay) {
        int applianceId = getApplianceId(inventoryId, relay);
        return applianceToCategoryMap.get(applianceId);
    }
    
    public Multimap<Integer, Integer> getInventoryToApplianceMultimap() {
        Multimap<Integer, Integer> multimap = ArrayListMultimap.create();
        for (Entry<Integer, Map<Integer, Integer>> entry : map.entrySet()) {
            multimap.putAll(entry.getKey(), entry.getValue().values());
        }
        return multimap;
    }
    
    public Map<Integer, Integer> getRelayApplianceMap(int inventoryId) {
        return map.get(inventoryId);
    }
}
