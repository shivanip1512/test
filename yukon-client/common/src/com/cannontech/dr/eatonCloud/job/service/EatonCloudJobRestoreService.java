package com.cannontech.dr.eatonCloud.job.service;

import java.util.Set;

import com.cannontech.loadcontrol.messages.LMEatonCloudStopCommand;

public interface EatonCloudJobRestoreService {

    /**
     * Starts jobs to send restore command to 2500 devices at a time
     */
    void createJobs(int programId, Set<Integer> devices, LMEatonCloudStopCommand command, int eventId);

}
