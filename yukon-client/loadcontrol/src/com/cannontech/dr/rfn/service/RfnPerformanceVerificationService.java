package com.cannontech.dr.rfn.service;

import java.util.List;

import org.joda.time.Instant;

import com.cannontech.common.util.Range;
import com.cannontech.dr.rfn.dao.impl.PerformanceVerificationDaoImpl.SortBy;
import com.cannontech.dr.rfn.model.PerformanceVerificationEventMessageDeviceStatus;

public interface RfnPerformanceVerificationService {
    
    /**
     * Starts a scheduled executor that sends a performance verification message to all 
     * enrolled RFN LCR devices in Yukon.
     */
    void schedulePerformanceVerificationMessaging();
    
    /**
     * For messages sent in {@code range} which currently have a status of UNKNOWN,
     * this method will return the asset availability status for each device. 
     * @Returns Map of DeviceId to asset availability
     */
    List<PerformanceVerificationEventMessageDeviceStatus> getAssetAvailabilityForUnknown(Range<Instant> dateRange,
                                                                     int numberPerPage, int pageNumber, SortBy sortBy);
}
