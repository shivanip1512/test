package com.cannontech.dr.rfn.service;

import java.util.List;
import java.util.Map;

import org.joda.time.Instant;

import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.util.Range;
import com.cannontech.dr.model.PerformanceVerificationAverageReports;
import com.cannontech.dr.model.PerformanceVerificationEventMessageStats;
import com.cannontech.dr.model.PerformanceVerificationMessageStatus;
import com.cannontech.dr.rfn.model.BroadcastEventDeviceDetails;
import com.cannontech.dr.rfn.model.DeviceStatus;

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
    
    /**
     * Return list of devices participated in a event.
     * This returns the list based on status passed, paging settings and subgroup passed.
     */
    List<BroadcastEventDeviceDetails> getDevicesWithStatus(long eventId, PerformanceVerificationMessageStatus[] statuses,
            PagingParameters pagingParameters, List<DeviceGroup> subGroups);

    /**
     * Return list of devices participated in a event.
     */
    List<BroadcastEventDeviceDetails> getAllDevicesWithStatus(long eventId);

    /**
     * Return count of devices participated in a event
     */
    int getTotalCount(long eventId, PerformanceVerificationMessageStatus[] statuses, List<DeviceGroup> subGroups);

    /**
     * Return performance verification statistics for a single event
     */
    PerformanceVerificationEventMessageStats getReportForEvent(long eventId);

    /**
     * Return status wise device count of unknown devices.
     */
    Map<DeviceStatus, Integer> getUnknownCounts(long eventId);
}
