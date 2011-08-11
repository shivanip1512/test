package com.cannontech.amr.scheduledGroupRequestExecution.dao.model;

import com.cannontech.amr.scheduledGroupRequestExecution.dao.ScheduledGroupRequestExecutionStatus;

public class ScheduledGroupRequestExecutionBundle {
    private int jobId;
    private ScheduledGroupRequestExecutionStatus status;
    private ScheduledGroupExecutionCounts executionCounts;

    public ScheduledGroupRequestExecutionBundle(int jobId, ScheduledGroupRequestExecutionStatus status,
                                                ScheduledGroupExecutionCounts executionCounts) {
        this.jobId = jobId;
        this.status = status;
        this.executionCounts = executionCounts;
    }

    public int getJobId() {
        return jobId;
    }

    public ScheduledGroupRequestExecutionStatus getStatus() {
        return status;
    }

    public ScheduledGroupExecutionCounts getExecutionCounts() {
        return executionCounts;
    }
}
