package com.cannontech.dr.assetavailability.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.model.SortingParameters;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.vendor.VendorSpecificSqlBuilderFactory;
import com.cannontech.dr.assetavailability.AssetAvailabilityCombinedStatus;
import com.cannontech.dr.assetavailability.AssetAvailabilityDetails;
import com.cannontech.dr.assetavailability.AssetAvailabilitySummary;
import com.cannontech.user.YukonUserContext;

public class MockAssetAvailabilityDao implements AssetAvailabilityDao {
    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;
    @Autowired private VendorSpecificSqlBuilderFactory vendorSpecificSqlBuilderFactory;
    private static final Logger log = YukonLogManager.getLogger(AssetAvailabilityDao.class);

    @Override
    public List<AssetAvailabilityDetails> getAssetAvailabilityDetails(Iterable<Integer> loadGroupIds,
            PagingParameters pagingParameters, AssetAvailabilityCombinedStatus[] filterCriteria,
            SortingParameters sortingParameters, Instant communicatingWindowEnd, Instant runtimeWindowEnd,
            Instant currentTime, YukonUserContext userContext) {
        List<AssetAvailabilityDetails> resultList = new ArrayList<AssetAvailabilityDetails>();

        AssetAvailabilityDetails assetAvailability = new AssetAvailabilityDetails();
        assetAvailability.setAppliances("LCR");
        assetAvailability.setSerialNumber("1234");
        assetAvailability.setType(HardwareType.LCR_4000);
        assetAvailability.setAvailability(AssetAvailabilityCombinedStatus.ACTIVE);
        resultList.add(assetAvailability);
        return resultList;
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
}
