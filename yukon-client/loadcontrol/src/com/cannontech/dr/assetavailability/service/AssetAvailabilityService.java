package com.cannontech.dr.assetavailability.service;

import java.util.Map;
import java.util.Set;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.dr.assetavailability.ApplianceAssetAvailabilitySummary;
import com.cannontech.dr.assetavailability.SimpleAssetAvailability;
import com.cannontech.dr.assetavailability.SimpleAssetAvailabilitySummary;

/**
 * Service for calculating the asset availability of DR devices and aggregate groups (load groups,
 * programs, control areas and scenarios).
 * 
 * Asset availability is calculated as follows:
 * -Active - The device has communicated within the communication window, and
 *  non-zero runtime has been reported from the device within the runtime window.
 * -Inactive - The device has communicated within the communication window,
 *  but no non-zero runtime has been reported from the device within the runtime window.
 * -Unavailable - The device has not communicated within the communication window.
 * -Opted-out - The device is opted-out.
 * 
 * The communications window defaults to 60 hours (2.5 days).
 * The runtime window defaults to 168 hours (1 week).
 */
public interface AssetAvailabilityService {
    
    /**
     * Gets a simple asset availability summary of all inventory in all load groups in the specified
     * DR grouping.
     * @throws IllegalArgumentException if the specified paoIdentifier is not a DR grouping.
     */
    public SimpleAssetAvailabilitySummary getAssetAvailabilityFromDrGroup(PaoIdentifier drPaoIdentifier);
    
    /**
     * Gets a simple asset availability summary of all inventory in all specified load groups.
     */
    public SimpleAssetAvailabilitySummary getAssetAvailabilityFromLoadGroups(Iterable<Integer> loadGroupIds);
    
    /**
     * Gets an asset availability summary of all appliances attached to inventory in any of the specified load groups.
     */
    public ApplianceAssetAvailabilitySummary getApplianceAssetAvailability(Iterable<Integer> loadGroupIds);
    
    /**
     * Gets an asset availability summary of all appliances in all load groups in the specified DR grouping.
     */
    public ApplianceAssetAvailabilitySummary getApplianceAssetAvailability(PaoIdentifier drPaoIdentifier);
    
    /**
     * @return The AssetAvailability for the specified inventory.
     */
    public SimpleAssetAvailability getAssetAvailability(int inventoryId);
    
    /**
     * @return A map of inventoryId to AssetAvailability for the specified inventory.
     */
    public Map<Integer, SimpleAssetAvailability> getAssetAvailability(Iterable<Integer> inventoryIds);
    
    /**
     * @return A map of inventoryId to AssetAvailability for all inventory in the specified DR
     * grouping.
     * @throws IllegalArgumentException if the specified paoIdentifier is not a DR grouping.
     */
    public Map<Integer, SimpleAssetAvailability> getAssetAvailability(PaoIdentifier paoIdentifier);
    
    /**
     * Returns the YukonPao for every device in the specified load group, program, scenario or control area whose asset
     * availability status is "unavailable".
     */
    public Set<YukonPao> getUnavailableDevicesInDrGrouping(PaoIdentifier drPaoIdentifier);
}
