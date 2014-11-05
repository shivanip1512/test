package com.cannontech.dr.assetavailability.dao;

import java.util.List;

import org.joda.time.Instant;

import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.model.SortingParameters;
import com.cannontech.dr.assetavailability.AssetAvailabilityCombinedStatus;
import com.cannontech.dr.assetavailability.AssetAvailabilityDetails;
import com.cannontech.dr.assetavailability.AssetAvailabilitySummary;
import com.cannontech.user.YukonUserContext;

/**
 * Dao for fetching asset availability details
 */
public interface AssetAvailabilityDao {
    /**
     * Creates AssetAvailabilityDetails list which have the details of assets
     * 
     * @param loadGroupIds -- list of load group ids.
     * @param pagingParameters -- paging details.
     * @param filters -- filter details.
     * @param sortingParameters -- Sorting details. Sorting order and direction are fetched from this.
     * @param communicatingWindowEnd -- communicating window end time.
     * @param runtimeWindowEnd -- runtime window end time
     * @param currentTime -- current Time.
     * @param userContext - user context used to extract the user details.
     * @return List of AssetAvailabilityDetails which have the details of assets.
     */
    List<AssetAvailabilityDetails> getAssetAvailabilityDetails(Iterable<Integer> loadGroupIds,
            PagingParameters pagingParameters, AssetAvailabilityCombinedStatus[] filters,
            SortingParameters sortingParameters, Instant communicatingWindowEnd, Instant runtimeWindowEnd,
            Instant currentTime, YukonUserContext userContext);

    /**
     * Creates an AssetAvailabilitySummary which has count of assets based on status
     * 
     * @param loadGroupIds -- list of load group ids.
     * @param communicatingWindowEnd -- communicating window end time.
     * @param runtimeWindowEnd -- runtime window end time.
     * @param currentTime -- current Time.
     * @return AssetAvailabilitySummary that has assets count.
     */
    AssetAvailabilitySummary getAssetAvailabilitySummary(Iterable<Integer> loadGroupIds,
            Instant communicatingWindowEnd, Instant runtimeWindowEnd, Instant currentTime);

}
