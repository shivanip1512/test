package com.cannontech.dr.assetavailability.service;

import java.util.Collection;
import java.util.Set;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.dr.assetavailability.AssetAvailability;
import com.cannontech.dr.assetavailability.SimpleAssetAvailabilitySummary;
import com.cannontech.dr.assetavailability.service.impl.NoInventoryException;

/**
 * Service for calculating the asset availability of DR devices and aggregate groups (load groups,
 * programs, control areas and scenarios).
 * 
 * Asset availability is calculated as follows:
 * -In communication; Running - The device has communicated within the communication window, and
 *  non-zero runtime has been reported from the device within the runtime window.
 * -In communication; Not Running - The device has communicated within the communication window,
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
     * dr grouping.
     */
    public SimpleAssetAvailabilitySummary getAssetAvailabilityFromDrGroup(PaoIdentifier drPaoIdentifier);
    
    /**
     * Gets a simple asset availability summary of all inventory in all specified load groups.
     */
    public SimpleAssetAvailabilitySummary getAssetAvailabilityFromLoadGroups(Collection<Integer> loadGroupIds);
    
    /**
     * @return The AssetAvailability for the specified device.
     * @throws NoInventoryException If the device has no associated inventory.
     */
    public AssetAvailability getAssetAvailability(int deviceId) throws NoInventoryException;
    
    /**
     * @return The AssetAvailability for the specified devices. An AssetAvailability is returned for
     * each deviceId. However, if the asset availability cannot be retrieved (e.g. no associated
     * inventory), the object will only contain the deviceId and null values.
     */
    public Set<AssetAvailability> getAssetAvailability(Collection<Integer> deviceIds);

}
