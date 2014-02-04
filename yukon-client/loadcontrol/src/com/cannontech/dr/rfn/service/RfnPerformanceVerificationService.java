package com.cannontech.dr.rfn.service;

import java.util.Map;

import org.joda.time.Instant;

import com.cannontech.common.util.Range;
import com.cannontech.dr.assetavailability.AssetAvailabilityStatus;

public interface RfnPerformanceVerificationService {
    
    /**
     * Starts a scheduled executor that sends a performance verification message to all 
     * enrolled RFN LCR devices in Yukon.
     */
    void schedulePerformanceVerificationMessaging();
    
    Map<Integer, AssetAvailabilityStatus> getAssetAvailabilityForUnknown(Range<Instant> dateRange, int numberPerPage,
                                                                         int pageNumber);
}
