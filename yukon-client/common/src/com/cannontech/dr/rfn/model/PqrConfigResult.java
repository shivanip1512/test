package com.cannontech.dr.rfn.model;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.cannontech.stars.database.data.lite.LiteLmHardwareBase;

public class PqrConfigResult {
    private Map<Integer, PqrDeviceConfigResult> inventoryResults;
    
    public PqrConfigResult(List<LiteLmHardwareBase> inventory, PqrConfig config) {
        inventoryResults = inventory.stream()
                                    .collect(Collectors.toMap(inv -> inv.getInventoryID(), 
                                                              inv -> new PqrDeviceConfigResult(inv, config)));
    }
    
    public PqrDeviceConfigResult getForInventoryId(int inventoryId) {
        return inventoryResults.get(inventoryId);
    }
    
    public boolean isComplete() {
        return inventoryResults.values()
                               .stream()
                               .filter(result -> !result.isComplete())
                               .findAny()
                               .isPresent();
    }
    
    public void complete() {
        for (PqrDeviceConfigResult result : inventoryResults.values()) {
            result.complete();
        }
    }
    
    public PqrConfigCounts getCounts() {
        PqrConfigCounts counts = new PqrConfigCounts();
        for (PqrDeviceConfigResult result : inventoryResults.values()) {
            counts.addResult(result.getOverallStatus());
        }
        return counts;
    }
}
