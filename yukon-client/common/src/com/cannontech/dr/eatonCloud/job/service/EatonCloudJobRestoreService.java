package com.cannontech.dr.eatonCloud.job.service;

import java.util.Set;

import com.cannontech.loadcontrol.messages.LMEatonCloudStopCommand;

public interface EatonCloudJobRestoreService {

    void createJobs(int programId, Set<Integer> devices, LMEatonCloudStopCommand command, Integer eventId);

}
