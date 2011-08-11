package com.cannontech.amr.scheduledGroupRequestExecution.service;

import com.cannontech.amr.scheduledGroupRequestExecution.dao.ScheduledGroupRequestExecutionStatus;

public interface ScheduledGroupRequestExecutionStatusService {
    /**
     * This bean (and in turn this method) has a scope of "request" (defined in applicationContext.xml) meaning 
     * that it is wiped out and a new one is created for every request. Use for dataUpdater calls or
     * anywhere this same value is being asked for in the scope of the same request
     * @return
     */
    public ScheduledGroupRequestExecutionStatus getStatus(int jobId);
}
