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
     * Returns A list of AssetAvailabilityDetails objects.
     */
    List<AssetAvailabilityDetails> getAssetAvailabilityDetails(Iterable<Integer> loadGroupIds,
            PagingParameters pagingParameters, String filters, String sortingOrder, String sortingDirection,
            String communicatingWindowEnd, String runtimeWindowEnd, String currentTime);

    /**
     * Returns A list of AssetAvailabilitySummary objects which have count of
     * assets based on status.
     */
    AssetAvailabilitySummary getAssetAvailabilitySummary(Iterable<Integer> loadGroupIds, String communicatingWindowEnd,
            String runtimeWindowEnd, String currentTime);

}
