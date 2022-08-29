package com.cannontech.dr.eatonCloud.job.service;

import java.util.Set;

import com.cannontech.loadcontrol.messages.LMEatonCloudScheduledCycleCommand;

public interface EatonCloudJobService {
    
    /**
     * Starts jobs that send shed command to 2500 devices at a time
     */
    void createJobs(int programId, Set<Integer> devices, LMEatonCloudScheduledCycleCommand command, int eventId);
    
    /**
     * Marks all devices that didn't respond for the event as failed and stops retries
     */
    void terminateEvent(int eventId);
}
