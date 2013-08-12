package com.cannontech.dr.assetavailability.service;

import java.util.Collection;
import java.util.Map;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.core.dynamic.exception.DynamicDataAccessException;
import com.cannontech.dr.assetavailability.SimpleAssetAvailability;
import com.cannontech.dr.assetavailability.SimpleAssetAvailabilitySummary;

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
     * @throws DynamicDataAccessException if the connection to dispatch is invalid
     */
    public SimpleAssetAvailabilitySummary getAssetAvailabilityFromDrGroup(PaoIdentifier drPaoIdentifier) throws DynamicDataAccessException;
    
    /**
     * Gets a simple asset availability summary of all inventory in all specified load groups.
     * @throws DynamicDataAccessException if the connection to dispatch is invalid
     */
    public SimpleAssetAvailabilitySummary getAssetAvailabilityFromLoadGroups(Collection<Integer> loadGroupIds) throws DynamicDataAccessException;
    
    /**
     * @return The AssetAvailability for the specified inventory.
     * @throws DynamicDataAccessException if the connection to dispatch is invalid
     */
    public SimpleAssetAvailability getAssetAvailability(int inventoryIds) throws DynamicDataAccessException;
    
    /**
     * @return A map of inventoryId to AssetAvailability for the specified inventory.
     * @throws DynamicDataAccessException if the connection to dispatch is invalid
     */
    public Map<Integer, SimpleAssetAvailability> getAssetAvailability(Collection<Integer> inventoryIds) throws DynamicDataAccessException;

}
