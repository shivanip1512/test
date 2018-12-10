package com.cannontech.dr.rfn.service;

import java.util.Map;

import org.joda.time.Instant;

import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.util.Range;
import com.cannontech.dr.model.PerformanceVerificationAverageReports;

public interface RfnPerformanceVerificationService {
    
    /**
     * Sends the performance verification message to all enrolled RFN LCR devices.
     */
    void sendPerformanceVerificationMessage();

    /**
     * Processes verification messages
     */
    void processVerificationMessages(YukonPao device, Map<Long, Instant> verificationMsgs, Range<Instant> range);

    PerformanceVerificationAverageReports getAverageReports();
    
    /**
     * Make entry of RFN event status in table RfnBroadcastEventSummary
     * for each day older than 180 days. Remove message from
     * RfnBroadcastEventDeviceStatus that are older than 180 days.
     */
    void archiveVerificationMessage();
}
