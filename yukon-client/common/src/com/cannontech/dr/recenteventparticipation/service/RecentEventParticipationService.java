package com.cannontech.dr.recenteventparticipation.service;

import java.util.List;

import org.joda.time.Instant;

import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.util.Range;
import com.cannontech.dr.controlaudit.ControlEventDeviceStatus;
import com.cannontech.dr.controlaudit.model.RecentEventParticipationDetail;
import com.cannontech.dr.controlaudit.model.RecentEventParticipationStats;
import com.cannontech.dr.controlaudit.model.RecentEventParticipationSummary;
import com.cannontech.dr.honeywellWifi.azure.event.EventPhase;

public interface RecentEventParticipationService {
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
     * Retrieves as a list of RecentEventParticipationSummary associated with the specified event IDs.
     */
    List<RecentEventParticipationSummary> getRecentEventParticipationSummary(int numberOfEvents);

    /**
     * Returned as a list of RecentEventParticipationStats associated with the specified range and based on paging
     * parameters
     */

    List<RecentEventParticipationStats> getRecentEventParticipationStats(Range<Instant> range, PagingParameters pagingParameters);

    /**
     * Returns RecentEventParticipationDetail for specified event
     */
    RecentEventParticipationDetail getRecentEventParticipationDetail(int eventId);

    /**
     * Returns list of ControlAuditDetail for specified date range
     */
    List<RecentEventParticipationDetail> getRecentEventParticipationDetails(Range<Instant> range);
}
