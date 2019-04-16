package com.cannontech.dr.recenteventparticipation.service;

import java.util.List;

import org.joda.time.Instant;

import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.util.Range;
import com.cannontech.dr.honeywellWifi.azure.event.EventPhase;
import com.cannontech.dr.recenteventparticipation.ControlEventDeviceStatus;
import com.cannontech.dr.recenteventparticipation.model.RecentEventParticipationDetail;
import com.cannontech.dr.recenteventparticipation.model.RecentEventParticipationStats;
import com.cannontech.dr.recenteventparticipation.model.RecentEventParticipationSummary;

public interface RecentEventParticipationService {
    /**
     * Update device status (({@link ControlEventDeviceStatus}, deviceReceivedTime)) for which response is
     * received.
     */
    public void updateDeviceControlEvent(int eventId, int deviceId, EventPhase eventPhase, Instant deviceReceivedTime);

    /**
     * Creates new control event associated with the specified load program & group
     */
    public void createDeviceControlEvent(int programId, long eventId, int groupId, Instant startTime, Instant stopTime);
    
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
     * Returns List of RecentEventParticipationDetail for specified event
     */
    List<RecentEventParticipationDetail> getRecentEventParticipationDetail(int eventId);

    /**
     * Returns list of RecentEventParticipationDetail for specified date range
     */
    List<RecentEventParticipationDetail> getRecentEventParticipationDetails(Range<Instant> range);

    /**
     * Returns number of ControlEvents for specified date range
     */
    int getNumberOfEvents(Range<Instant> range);
}
