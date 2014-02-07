package com.cannontech.dr.rfn.service;

import java.util.Map;

import org.joda.time.Instant;

import com.cannontech.common.util.Range;

public interface RfnPerformanceVerificationService {
    
    /**
     * Sends the performance verification message to all enrolled RFN LCR devices.
     */
    public void sendPerformanceVerificationMessage();

	/**
	 * Processes verification messages
	 */
	public void processVerificationMessages(int deviceId, Map<Long, Instant> verificationMsgs, Range<Instant> range);
}
