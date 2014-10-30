package com.cannontech.dr.assetavailability.dao;

import java.util.List;

import com.cannontech.common.model.PagingParameters;
import com.cannontech.dr.assetavailability.AssetAvailabilityDetails;
import com.cannontech.dr.assetavailability.AssetAvailabilitySummary;

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
     * @param sortingOrder --order for sorting.
     * @param sortingDirection - direction for sorting.
     * @param communicatingWindowEnd -- communicating window end time.
     * @param runtimeWindowEnd -- runtime window end time
     * @param currentTime -- current Time.
     * @return List of AssetAvailabilityDetails which have the details of assets.
     */
    List<AssetAvailabilityDetails> getAssetAvailabilityDetails(Iterable<Integer> loadGroupIds,
            PagingParameters pagingParameters, String filters, String sortingOrder, String sortingDirection,
            String communicatingWindowEnd, String runtimeWindowEnd, String currentTime);

    /**
     * Creates an AssetAvailabilitySummary which has count of assets based on status
     * 
     * @param loadGroupIds -- list of load group ids.
     * @param communicatingWindowEnd -- communicating window end time.
     * @param runtimeWindowEnd -- runtime window end time.
     * @param currentTime -- current Time.
     * @return AssetAvailabilitySummary that has assets count.
     */
    AssetAvailabilitySummary getAssetAvailabilitySummary(Iterable<Integer> loadGroupIds, String communicatingWindowEnd,
            String runtimeWindowEnd, String currentTime);

}
