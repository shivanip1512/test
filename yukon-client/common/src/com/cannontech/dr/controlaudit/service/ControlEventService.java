package com.cannontech.dr.controlaudit.service;

import java.util.List;

import org.joda.time.Instant;

import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.model.SortingParameters;
import com.cannontech.common.util.Range;
import com.cannontech.dr.controlaudit.ControlEventDeviceStatus;
import com.cannontech.dr.controlaudit.model.ControlAuditDetail;
import com.cannontech.dr.controlaudit.model.ControlAuditStats;
import com.cannontech.dr.controlaudit.model.ControlAuditSummary;
import com.cannontech.dr.honeywellWifi.azure.event.EventPhase;

public interface ControlEventService {
    /**
     * Update device status (({@link ControlEventDeviceStatus}, deviceRecievedTime)) for which response is
     * received.
     */
    public void updateDeviceControlEvent(int eventId, int deviceId, EventPhase eventPhase, Instant deviceRecievedTime);

    /**
     * Creates new control event associated with the specified load group
     */
    public void createDeviceControlEvent(int eventId, int groupId, Instant startTime, Instant stopTime);
    
    /**
     * Retrieves the most recent event summary information associated with the specified event IDs.
     */
    List<ControlAuditSummary> getControlAuditSummary(int numberOfEvents);

    /**
     * Returned as a list of ControlAuditStats associated with the specified range and based on paging and
     * sorting parameters
     */

    List<ControlAuditStats> getControlAuditStats(Range<Instant> range, PagingParameters pagingParameters,
            SortingParameters sortingParameters);

    /**
     * Returns ControlAuditDetail for specified event
     */
    ControlAuditDetail getControlAuditDetail(int eventId);

    /**
     * Returns list of ControlAuditDetail for specified date range
     */
    List<ControlAuditDetail> getControlAuditDetails(Range<Instant> range);
}
