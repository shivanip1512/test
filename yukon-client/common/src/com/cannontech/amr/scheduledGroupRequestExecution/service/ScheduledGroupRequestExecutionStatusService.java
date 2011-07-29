package com.cannontech.amr.scheduledGroupRequestExecution.service;

import com.cannontech.amr.scheduledGroupRequestExecution.dao.ScheduledGroupRequestExecutionStatus;

public interface ScheduledGroupRequestExecutionStatusService {
    public ScheduledGroupRequestExecutionStatus getStatus(int jobId);
}
