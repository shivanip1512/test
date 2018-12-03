package com.cannontech.dr.assetavailability.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.model.SortingParameters;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.dr.assetavailability.ApplianceAssetAvailabilityDetails;
import com.cannontech.dr.assetavailability.ApplianceAssetAvailabilitySummary;
import com.cannontech.dr.assetavailability.AssetAvailabilityCombinedStatus;
import com.cannontech.dr.assetavailability.AssetAvailabilityDetails;
import com.cannontech.dr.assetavailability.AssetAvailabilitySummary;
import com.cannontech.dr.assetavailability.SimpleAssetAvailability;
import com.cannontech.user.YukonUserContext;

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
     * Gets a asset availability summary of all inventory in all load groups in the specified
     * DR grouping.
     * 
     * @param drPaoIdentifier - Identify a program, load group, control area or scenario.
     * @return AssetAvailabilitySummary which has count based on status.
     */
    public AssetAvailabilitySummary getAssetAvailabilityFromDrGroup(PaoIdentifier drPaoIdentifier);
    
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
     * Gets the details of assets for a program/load group/control area/scenario based on filters and paging
     * provided.
     * 
     * @param paoIdentifier - Identify a program, load group, control area or scenario.
     * @param paging - paging details.
     * @param filters - List of selected filters.
     * @param sortBy - sorting order.
     * @param userContext - used to get the user details
     * @return SearchResults of ApplianceAssetAvailabilityDetails.
     */
    public SearchResults<ApplianceAssetAvailabilityDetails> getAssetAvailabilityWithAppliance(PaoIdentifier paoIdentifier, PagingParameters 
            paging, AssetAvailabilityCombinedStatus[] filters, SortingParameters sortBy, YukonUserContext userContext);

    /**
     * Returns every device in the specified load group, program, scenario or control area whose asset
     * availability status is "unavailable".
     */
    public Set<SimpleDevice> getUnavailableDevicesInDrGrouping(YukonPao yukonPao);

    /**
     * Gets the details of assets for a program/load group/control area/scenario based on subGroups, filters and paging
     * provided.
     */
    SearchResults<AssetAvailabilityDetails> getAssetAvailabilityDetails(List<DeviceGroup> subGroups,
            PaoIdentifier paoIdentifier, PagingParameters paging, AssetAvailabilityCombinedStatus[] filters,
            SortingParameters sortBy, YukonUserContext userContext);

}
