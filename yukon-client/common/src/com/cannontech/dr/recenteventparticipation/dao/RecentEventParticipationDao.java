package com.cannontech.dr.recenteventparticipation.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.joda.time.Instant;

import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.util.Range;
import com.cannontech.dr.recenteventparticipation.ControlEventDeviceStatus;
import com.cannontech.dr.recenteventparticipation.model.ControlDeviceDetail;
import com.cannontech.dr.recenteventparticipation.model.RecentEventParticipationDetail;
import com.cannontech.dr.recenteventparticipation.model.RecentEventParticipationStats;
import com.cannontech.dr.recenteventparticipation.model.RecentEventParticipationSummary;

public interface RecentEventParticipationDao {
    /**
     * Creates new control event associated with the specified load program & group
     */
    public void createNewEventMapping(int programId, long eventId, int groupId, Instant startTime, Instant stopTime);

    /**
     * Update device status (({@link ControlEventDeviceStatus}, deviceReceivedTime)) for which response is
     * received.
     */
    void updateDeviceControlEvent(int eventId, int deviceId, List<ControlEventDeviceStatus> skipUpdateForStatus,
            ControlEventDeviceStatus receivedMessageStatus, Instant deviceReceivedTime);

    /**
     * Insert event information for the device (in the specified load group) for which event as sent.
     * 
     * @param eventTime -- start time of event (eventTime is used to load OptOutEventId where this parameter is
     *        within the start/stop of OptOut Event ).
     */
    void insertDeviceControlEvent(long eventId, int loadGroupId, Instant eventTime);

    /**
     * Retrieves as a list of RecentEventParticipationSummary associated with the specified event IDs.
     */

    List<RecentEventParticipationSummary> getRecentEventParticipationSummary(int numberOfEvents);

    /**
     * Returned as a list of RecentEventParticipationStats associated with the specified range and based on paging parameters
     */

    List<RecentEventParticipationStats> getRecentEventParticipationStats(Range<Instant> range, PagingParameters pagingParameters);

    /**
     * Returns List of RecentEventParticipationDetail for specified event
     */
    List<RecentEventParticipationDetail> getRecentEventParticipationDetail(long eventId);

    /**
     * Returns list of RecentEventParticipationDetail for specified date range
     */
    List<RecentEventParticipationDetail> getRecentEventParticipationDetails(Range<Instant> range);

    /**
     * Returns list of ControlDeviceDetail for specified event
     */
    List<ControlDeviceDetail> getControlEventDeviceData(int eventId);
    
    /**
     * Gets status count for devices for specified date range
     */
    Map<Integer, Integer> getControlEventDeviceStatus(List<Integer> deviceId, Date startDate, Date endDate);

    /**
     * Gets count of ControlEvents for specified date range
     */
    int getNumberOfEvents(Range<Instant> range);
}
