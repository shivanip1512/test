package com.cannontech.dr.eatonCloud.job.service;

import java.util.List;

import com.cannontech.dr.eatonCloud.job.service.impl.EventRestoreSummary;

public interface EatonCloudJobRestorePollService {

    void schedulePoll(EventRestoreSummary summary, int pollInMinutes, List<String> jobGuids, List<String> devicesGuids);
}
