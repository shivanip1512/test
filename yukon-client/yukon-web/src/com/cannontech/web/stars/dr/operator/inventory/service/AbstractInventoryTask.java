package com.cannontech.web.stars.dr.operator.inventory.service;

import com.cannontech.common.util.CancelStatus;
import com.cannontech.common.util.Completable;
import com.cannontech.user.YukonUserContext;

public abstract class AbstractInventoryTask implements Completable, CancelStatus {
    
    public static enum Status {
        UNSUPPORTED, SUCCESS, FAIL
    }
    
    protected int completedItems;
    protected int successCount;
    protected int failedCount;
    protected int unsupportedCount;
    protected YukonUserContext userContext;
    protected String taskId;
    protected boolean canceled = false;
    
    public String getTaskId() {
        return taskId;
    }
    
    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }
    
    public abstract long getTotalItems();
    
    public int getCompletedItems() {
        return completedItems;
    }
    
    @Override
    public boolean isComplete() {
        return getTotalItems() == completedItems;
    }
    
    @Override
    public boolean isCanceled() {
        return canceled;
    }
    
    /**
     * Sets the canceled flag on the task so that the process will know
     * to stop working on this task, returns true if the task was canceled
     * before the task was complete.
     * @return Returns true when cancel was effective (task was not already complete)
     */
    public boolean cancel() {
        canceled = true;
        return !isComplete();
    }
    
    public abstract Runnable getProcessor();

    public int getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(int successCount) {
        this.successCount = successCount;
    }

    public int getFailedCount() {
        return failedCount;
    }

    public void setFailedCount(int failedCount) {
        this.failedCount = failedCount;
    }
    
    public int getUnsupportedCount() {
        return unsupportedCount;
    }
    
    public void setUnsupportedCount(int unsupportedCount) {
        this.unsupportedCount = unsupportedCount;
    }
}