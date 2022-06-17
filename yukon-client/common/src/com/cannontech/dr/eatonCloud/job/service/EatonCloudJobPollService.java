package com.cannontech.dr.eatonCloud.job.service;

import com.cannontech.dr.eatonCloud.job.service.impl.EventSummary;

public interface EatonCloudJobPollService {

    /**
     * Schedules job poll. Job poll returns device status.
     * 
     * @param minutes      - schedule in X minutes from now
     * @param totalDevices - total devices for the event, needed for send smart notifications
     */
    void schedulePoll(EventSummary summary, int minutes, int totalDevices);

    /**
     * Starts job poll. Job poll returns device status.
     */
    void immediatePoll(EventSummary summary);

}
