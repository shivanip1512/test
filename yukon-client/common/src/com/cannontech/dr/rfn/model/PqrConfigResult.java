package com.cannontech.dr.rfn.model;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.cannontech.stars.database.data.lite.LiteLmHardwareBase;

/**
 * Contains the results of a Power Quality Response configuration operation for a set of devices.
 * The results will be updated as the configurations are sent.
 */
public class PqrConfigResult {
    private Map<Integer, PqrDeviceConfigResult> inventoryResults;
    
    /**
     * Create a new result for the specified supported and unsupported devices. The supported devices will have their
     * status set to "in progress", and the unsupported devices will be set to "unsupported".
     */
    public PqrConfigResult(List<LiteLmHardwareBase> supportedDevices, List<LiteLmHardwareBase> unsupportedDevices, PqrConfig config) {
        inventoryResults = supportedDevices.stream()
                                           .collect(Collectors.toMap(inv -> inv.getInventoryID(), 
                                                                     inv -> new PqrDeviceConfigResult(inv, config)));
        Map<Integer, PqrDeviceConfigResult> unsupportedResults = 
                unsupportedDevices.stream()
                                  .collect(Collectors.toMap(inv -> inv.getInventoryID(), 
                                                            inv -> PqrDeviceConfigResult.unsupportedResult(inv)));
        inventoryResults.putAll(unsupportedResults);
    }
    
    /**
     * Get the individual result for the device with the specified ID.
     */
    public PqrDeviceConfigResult getForInventoryId(int inventoryId) {
        return inventoryResults.get(inventoryId);
    }
    
    /**
     * Checks to see if all devices in this result have a completed status (success, failed, or unsupported).
     */
    public boolean isComplete() {
        if (inventoryResults.size() == 0) {
            return true;
        }
        
        return inventoryResults.values()
                               .stream()
                               .filter(result -> !result.isComplete())
                               .findAny()
                               .isPresent();
    }
    
    /**
     * Marks the entire result as complete by setting all "in progress" devices to "failed".
     */
    public void complete() {
        for (PqrDeviceConfigResult result : inventoryResults.values()) {
            result.complete();
        }
    }
    
    /**
     * Gets a count of the number of devices in this result with each status.
     */
    public PqrConfigCounts getCounts() {
        PqrConfigCounts counts = new PqrConfigCounts();
        for (PqrDeviceConfigResult result : inventoryResults.values()) {
            counts.addResult(result.getOverallStatus());
        }
        return counts;
    }
}
