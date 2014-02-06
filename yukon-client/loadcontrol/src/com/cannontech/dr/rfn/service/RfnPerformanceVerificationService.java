package com.cannontech.dr.rfn.service;

import java.util.Map;

import org.joda.time.Instant;

import com.cannontech.common.util.Range;
import com.cannontech.dr.assetavailability.AssetAvailabilityStatus;

public interface RfnPerformanceVerificationService {
    
    /**
     * For messages sent in {@code range} which currently have a status of UNKNOWN,
     * this method will return the asset availability status for each device. 
     * @Returns Map of DeviceId to asset availability
     */
    Map<Integer, AssetAvailabilityStatus> getAssetAvailabilityForUnknown(long messageId);
    
	/**
	 * Processes verification messages
	 */
	public void processVerificationMessages(int deviceId,
			Map<Long, Instant> verificationMsgs, Range<Instant> range);
}
