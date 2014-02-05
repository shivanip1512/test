package com.cannontech.dr.rfn.service;

import java.util.Map;

import com.cannontech.dr.assetavailability.AssetAvailabilityStatus;

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
    Map<Integer, AssetAvailabilityStatus> getAssetAvailabilityForUnknown(long messageId);
}
