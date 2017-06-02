package com.cannontech.dr.controlaudit.dao;

import java.util.List;

import org.joda.time.Instant;

import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.model.SortingParameters;
import com.cannontech.common.util.Range;
import com.cannontech.dr.controlaudit.ControlEventDeviceStatus;
import com.cannontech.dr.controlaudit.model.ControlAuditStats;
import com.cannontech.dr.controlaudit.model.ControlAuditSummary;

public interface ControlEventDao {
    /**
     * Creates new control event associated with the specified load group
     */
    public void createNewEventMapping(int eventId, int groupId, Instant startTime, Instant stopTime);

    /**
     * Update device status (({@link ControlEventDeviceStatus}, deviceRecievedTime)) for which response is
     * received.
     */
    void updateDeviceControlEvent(int eventId, int deviceId, List<ControlEventDeviceStatus> skipUpdateForStatus,
            ControlEventDeviceStatus recievedMessageStatus, Instant deviceRecievedTime);

    /**
     * Insert event information for the device (in the specified load group) for which event as sent.
     */
    void insertDeviceControlEvent(int eventId, int loadGroupId);

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
}
