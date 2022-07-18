package com.cannontech.dr.eatonCloud.job.service;

import java.util.List;

import org.joda.time.Instant;
import org.joda.time.Minutes;

import com.cannontech.dr.eatonCloud.job.service.impl.EventSummary;

public interface EatonCloudJobPollService {

    /**
     * Schedules job poll. Job poll returns device status.
     * 
     * @param minutes      - schedule in X minutes from now
     * @param totalDevices - total devices for the event, needed for send smart notifications
     */
    void schedulePoll(EventSummary summary, Minutes minutes, int totalDevices, List<String> jobGuids, Instant jobCreationTime, int currentTry);

    /**
     * Starts job poll. Job poll returns device status.
     */
    void immediatePoll(EventSummary summary, List<String> jobGuids, Instant jobCreationTime, int currentTry);

    void failWillRetryDevicesAfterLastPoll(EventSummary summary);
}
