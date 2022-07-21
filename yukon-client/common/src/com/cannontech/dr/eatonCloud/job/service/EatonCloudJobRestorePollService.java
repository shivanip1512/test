package com.cannontech.dr.eatonCloud.job.service;

import java.util.List;
import java.util.Map;

import org.joda.time.Minutes;

import com.cannontech.dr.eatonCloud.job.service.impl.EventRestoreSummary;

public interface EatonCloudJobRestorePollService {

    /**
     * Schedules job poll. Job poll returns device status.
     * 
     * @param minutes      - schedule in X minutes from now
     */
    void schedulePoll(EventRestoreSummary summary, Minutes minutes, Map<String, List<String>> jobGuids, List<String> allDevicesGuids);
}
