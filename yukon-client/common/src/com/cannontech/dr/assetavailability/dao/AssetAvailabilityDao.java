package com.cannontech.dr.assetavailability.dao;

import java.util.List;

import org.joda.time.Instant;

import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.model.SortingParameters;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.dr.assetavailability.ApplianceAssetAvailabilityDetails;
import com.cannontech.dr.assetavailability.ApplianceAssetAvailabilitySummary;
import com.cannontech.dr.assetavailability.AssetAvailabilityCombinedStatus;
import com.cannontech.dr.assetavailability.AssetAvailabilityDetails;
import com.cannontech.dr.assetavailability.AssetAvailabilitySummary;
import com.cannontech.user.YukonUserContext;

/**
 * Dao for fetching asset availability details
 */
public interface AssetAvailabilityDao {

    public enum SortBy {

        SERIALNUM("SERIAL_NUM"), 
        TYPE("TYPE"), 
        LASTCOMM("LAST_COMM"), 
        LASTRUN("LAST_RUN"),
        GATEWAYID("GATEWAY_ID");

        private final String dbString;

        private SortBy(String dbString) {
            this.dbString = dbString;
        }

        public String getDbString() {
            return dbString;
        }

    }

    /**
     * Creates ApplianceAssetAvailabilityDetails list which have the details of assets
     * 
     * @param loadGroupIds -- list of load group ids.
     * @param pagingParameters -- paging details.
     * @param filters -- filter details.
     * @param sortingParameters -- Sorting details. Sorting order and direction are fetched from this.
     * @param communicatingWindowEnd -- communicating window end time.
     * @param runtimeWindowEnd -- runtime window end time
     * @param currentTime -- current Time.
     * @param userContext - user context used to extract the user details.
     * @return SearchResults of ApplianceAssetAvailabilityDetails which have the details of assets.
     */
    SearchResults<ApplianceAssetAvailabilityDetails> getAssetAvailabilityDetailsWithAppliance(Iterable<Integer> loadGroupIds,
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
    
    /**
     * Creates an ApplianceAssetAvailabilitySummary which has status of appliance
     * 
     * @param drPaoIdentifier -- Pao identifier for which appliances and status has to be fetched.
     * @param communicatingWindowEnd -- communicating window end time.
     * @param runtimeWindowEnd -- runtime window end time.
     * @param currentTime -- current Time.
     * @return ApplianceAssetAvailabilitySummary that has appliances and their status.
     */    
    ApplianceAssetAvailabilitySummary getApplianceAssetAvailabilitySummary(PaoIdentifier drPaoIdentifier,
            Instant communicatingWindowEnd, Instant runtimeWindowEnd,Instant currentTime);

    /**
     * Creates AssetAvailabilityDetails list which have the details of assets
     * 
     * @param subGroups  -- list of selected Device Groups
     * @param loadGroupIds -- list of load group ids.
     * @param pagingParameters -- paging details.
     * @param filters -- filter details.
     * @param sortBy -- Sorting details. Sorting order and direction are fetched from this.
     * @param direction -- Sorting direction details.
     * @param communicatingWindowEnd -- communicating window end time.
     * @param runtimeWindowEnd -- runtime window end time
     * @param currentTime -- current Time.
     * @param userContext - user context used to extract the user details.
     * @return SearchResults of AssetAvailabilityDetails which have the details of assets.
     */
    SearchResults<AssetAvailabilityDetails> getAssetAvailabilityDetails(List<DeviceGroup> subGroups,
            Iterable<Integer> loadGroupIds, PagingParameters pagingParameters,
            AssetAvailabilityCombinedStatus[] filterCriteria, SortBy sortBy, Direction direction,
            Instant communicatingWindowEnd, Instant runtimeWindowEnd, Instant currentTime,
            YukonUserContext userContext);

}
