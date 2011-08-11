package com.cannontech.amr.scheduledGroupRequestExecution.dao.model;

public class ScheduledGroupExecutionCounts {
    private int failureCount;
    private int successCount;
    private int totalCount;

    public ScheduledGroupExecutionCounts(int failureCount, int successCount, int totalCount) {
        this.failureCount = failureCount;
        this.successCount = successCount;
        this.totalCount = totalCount;
    }
    public int getFailureCount() {
        return failureCount;
    }
    public void setFailureCount(int failureCount) {
        this.failureCount = failureCount;
    }
    public int getSuccessCount() {
        return successCount;
    }
    public void setSuccessCount(int successCount) {
        this.successCount = successCount;
    }
    public int getTotalCount() {
        return totalCount;
    }
    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

}
