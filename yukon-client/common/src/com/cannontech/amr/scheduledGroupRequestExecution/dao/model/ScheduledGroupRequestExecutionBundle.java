package com.cannontech.amr.scheduledGroupRequestExecution.dao.model;

import com.cannontech.amr.scheduledGroupRequestExecution.dao.ScheduledGroupRequestExecutionStatus;

public class ScheduledGroupRequestExecutionBundle {
    private int jobId;
    private ScheduledGroupRequestExecutionStatus status;
    private int failureCount;
    private int successCount;
    private int totalCount;

    public ScheduledGroupRequestExecutionBundle(int jobId, ScheduledGroupRequestExecutionStatus status,
                                                int failureCount, int successCount, int totalCount) {
        this.jobId = jobId;
        this.status = status;
        this.failureCount = failureCount;
        this.successCount = successCount;
        this.totalCount = totalCount;
    }

    public int getJobId() {
        return jobId;
    }

    public ScheduledGroupRequestExecutionStatus getStatus() {
        return status;
    }

    public int getFailureCount() {
        return failureCount;
    }

    public int getSuccessCount() {
        return successCount;
    }

    public int getTotalCount() {
        return totalCount;
    }
}
