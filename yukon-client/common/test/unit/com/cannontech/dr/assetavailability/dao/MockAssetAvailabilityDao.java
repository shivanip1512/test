package com.cannontech.dr.assetavailability.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.joda.time.Instant;

import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.inventory.HardwareType;
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
import com.google.common.collect.Sets;

public class MockAssetAvailabilityDao implements AssetAvailabilityDao {

    @Override
    public SearchResults<ApplianceAssetAvailabilityDetails> getAssetAvailabilityDetailsWithAppliance(Iterable<Integer> loadGroupIds,
            PagingParameters pagingParameters, AssetAvailabilityCombinedStatus[] filterCriteria,
            SortingParameters sortingParameters, Instant communicatingWindowEnd, Instant runtimeWindowEnd,
            Instant currentTime, YukonUserContext userContext) {
        List<ApplianceAssetAvailabilityDetails> resultList = new ArrayList<ApplianceAssetAvailabilityDetails>();

        ApplianceAssetAvailabilityDetails assetAvailability = new ApplianceAssetAvailabilityDetails();
        assetAvailability.setAppliances("LCR");
        assetAvailability.setSerialNumber("1234");
        assetAvailability.setType(HardwareType.LCR_4000);
        assetAvailability.setAvailability(AssetAvailabilityCombinedStatus.ACTIVE);
        resultList.add(assetAvailability);
        SearchResults<ApplianceAssetAvailabilityDetails> result = SearchResults.pageBasedForSublist(resultList, 1, 10, 20);
        
        return result;
    }

    @Override
    public AssetAvailabilitySummary getAssetAvailabilitySummary(Iterable<Integer> loadGroupIds,
            Instant communicatingWindowEnd, Instant runtimeWindowEnd, Instant currentTime) {
  
        AssetAvailabilitySummary assetAvailabilitySummary = new AssetAvailabilitySummary();
        assetAvailabilitySummary.setActiveSize(2);
        assetAvailabilitySummary.setInactiveSize(1);
        assetAvailabilitySummary.setUnavailableSize(1);
        assetAvailabilitySummary.setOptedOutSize(4);
        
        return assetAvailabilitySummary;
    }

    @Override
    public ApplianceAssetAvailabilitySummary getApplianceAssetAvailabilitySummary(PaoIdentifier drPaoIdentifier,
            Instant communicatingWindowEnd, Instant runtimeWindowEnd, Instant currentTime) {
        
        Set<Integer> expectedAllAppliances = Sets.newHashSet(10011, 10021, 10031, 10032, 10041, 10051, 10061, 10071, 10081);
        ApplianceAssetAvailabilitySummary summary = new ApplianceAssetAvailabilitySummary(expectedAllAppliances);
        summary.addOptedOut(Sets.newHashSet(10021, 10061, 10071, 10081));
        summary.addCommunicating(Sets.newHashSet(10011, 10032,10031, 10041));
        summary.addRunning(Sets.newHashSet(10011, 10032));
        return summary;
    }

    @Override
    public SearchResults<AssetAvailabilityDetails> getAssetAvailabilityDetails(List<DeviceGroup> subGroups,
            Iterable<Integer> loadGroupIds, PagingParameters pagingParameters,
            AssetAvailabilityCombinedStatus[] filterCriteria, SortingParameters sortingParameters,
            Instant communicatingWindowEnd, Instant runtimeWindowEnd, Instant currentTime,
            YukonUserContext userContext) {
        List<AssetAvailabilityDetails> resultList = new ArrayList<AssetAvailabilityDetails>();

        AssetAvailabilityDetails assetAvailability = new AssetAvailabilityDetails();
        assetAvailability.setInventoryId(121234);
        assetAvailability.setDeviceId(0);
        assetAvailability.setSerialNumber("1234");
        assetAvailability.setType(HardwareType.LCR_4000);
        assetAvailability.setAvailability(AssetAvailabilityCombinedStatus.ACTIVE);
        resultList.add(assetAvailability);
        SearchResults<AssetAvailabilityDetails> result = SearchResults.pageBasedForSublist(resultList, 1, 10, 20);

        return result;
    }

}
