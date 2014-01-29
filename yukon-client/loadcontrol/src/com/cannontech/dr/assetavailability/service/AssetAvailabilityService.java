package com.cannontech.dr.assetavailability.service;

import java.util.Map;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.core.dynamic.exception.DynamicDataAccessException;
import com.cannontech.dr.assetavailability.ApplianceAssetAvailabilitySummary;
import com.cannontech.dr.assetavailability.AssetAvailabilityStatus;
import com.cannontech.dr.assetavailability.AssetAvailabilityTotals;
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
     * @throws DynamicDataAccessException if the connection to dispatch is invalid.
     * @throws IllegalArgumentException if the specified paoIdentifier is not a DR grouping.
     */
    public SimpleAssetAvailabilitySummary getAssetAvailabilityFromDrGroup(PaoIdentifier drPaoIdentifier) throws DynamicDataAccessException;
    
    /**
     * Gets a simple asset availability summary of all inventory in all specified load groups.
     * @throws DynamicDataAccessException if the connection to dispatch is invalid.
     */
    public SimpleAssetAvailabilitySummary getAssetAvailabilityFromLoadGroups(Iterable<Integer> loadGroupIds) throws DynamicDataAccessException;
    
    /**
     * Gets an asset availability summary of all appliances attached to inventory in any of the specified load groups.
     * @throws DynamicDataAccessException if the connection to dispatch is invalid.
     */
    public ApplianceAssetAvailabilitySummary getApplianceAssetAvailability(Iterable<Integer> loadGroupIds) throws DynamicDataAccessException;
    
    /**
     * Gets an asset availability summary of all appliances in all load groups in the specified DR grouping.
     * @throws DynamicDataAccessException if the connection to dispatch is invalid.
     */
    public ApplianceAssetAvailabilitySummary getApplianceAssetAvailability(PaoIdentifier drPaoIdentifier) throws DynamicDataAccessException;
    
    /**
     * @return The AssetAvailability for the specified inventory.
     * @throws DynamicDataAccessException if the connection to dispatch is invalid.
     */
    public SimpleAssetAvailability getAssetAvailability(int inventoryId) throws DynamicDataAccessException;
    
    /**
     * @return A map of inventoryId to AssetAvailability for the specified inventory.
     * @throws DynamicDataAccessException if the connection to dispatch is invalid.
     */
    public Map<Integer, SimpleAssetAvailability> getAssetAvailability(Iterable<Integer> inventoryIds) throws DynamicDataAccessException;
    
    /**
     * @return A map of inventoryId to AssetAvailability for all inventory in the specified DR
     * grouping.
     * @throws DynamicDataAccessException if the connection to dispatch is invalid.
     * @throws IllegalArgumentException if the specified paoIdentifier is not a DR grouping.
     */
    public Map<Integer, SimpleAssetAvailability> getAssetAvailability(PaoIdentifier paoIdentifier) throws DynamicDataAccessException;
    
    /**
     * Retrieves the total number of active, inactive and unavailable inventory in the specified collection.
     */
    public AssetAvailabilityTotals getAssetAvailabilityTotal(Iterable<Integer> inventoryIds);
    
    /**
     * Retrieves the individual active, inactive or unavailable status of the inventory in the specified collection.
     * Note that inventory that have never communicated will have a null AssetAvailabilityStatus.
     */
    public Map<Integer, AssetAvailabilityStatus> getAssetAvailabilityStatus(Iterable<Integer> inventoryIds);

}
