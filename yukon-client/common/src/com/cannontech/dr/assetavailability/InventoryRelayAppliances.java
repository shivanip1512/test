package com.cannontech.dr.assetavailability;

import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

public final class InventoryRelayAppliances {
    private final Map<Integer, Map<Integer, Integer>> map = Maps.newHashMap();
    
    public InventoryRelayAppliances(Iterable<InventoryRelayAppliance> iras) {
        for(InventoryRelayAppliance ira : iras) {
            add(ira);
        }
    }
    
    public void add(InventoryRelayAppliance ira) {
        if(map.containsKey(ira.getInventoryId())) {
            map.get(ira.getInventoryId()).put(ira.getRelay(), ira.getApplianceId());
        } else {
            Map<Integer, Integer> relayMap = Maps.newHashMap();
            relayMap.put(ira.getRelay(), ira.getApplianceId());
            map.put(ira.getInventoryId(), relayMap);
        }
    }
    
    public Integer getApplianceId(int inventoryId, int relay) {
        return map.get(inventoryId).get(relay);
    }
    
    public Multimap<Integer, Integer> getInventoryToApplianceMultimap() {
        Multimap<Integer, Integer> multimap = ArrayListMultimap.create();
        for(Entry<Integer, Map<Integer, Integer>> entry : map.entrySet()) {
            multimap.putAll(entry.getKey(), entry.getValue().values());
        }
        return multimap;
    }
}
