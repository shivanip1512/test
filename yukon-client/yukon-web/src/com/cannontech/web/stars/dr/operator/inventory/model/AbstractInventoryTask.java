package com.cannontech.web.stars.dr.operator.inventory.model;

import org.joda.time.Instant;

import com.cannontech.common.i18n.Displayable;
import com.cannontech.common.util.Cancelable;
import com.cannontech.common.util.Completable;
import com.cannontech.user.YukonUserContext;

public abstract class AbstractInventoryTask implements Completable, Cancelable, Displayable, Comparable<AbstractInventoryTask> {
    
    protected String taskId;
    protected long startedAt;
    protected int completedItems;
    protected int successCount;
    protected int failedCount;
    protected int unsupportedCount;
    protected YukonUserContext userContext;
    protected boolean canceled;
    
    protected static final String key = "yukon.web.modules.operator.inventory.actions.";
    
    public AbstractInventoryTask() {
        this.startedAt = Instant.now().getMillis();
    }
    
    public String getTaskId() {
        return taskId;
    }
    
    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }
    
    public long getStartedAt() {
        return startedAt;
    }
    
    public void setStartedAt(long startedAt) {
        this.startedAt = startedAt;
    }
    
    public abstract long getTotalItems();
    
    public int getCompletedItems() {
        return completedItems;
    }
    
    @Override
    public boolean isComplete() {
        return getTotalItems() == completedItems;
    }
    
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
    
    public YukonUserContext getUserContext() {
        return userContext;
    }
    
    public void setUserContext(YukonUserContext userContext) {
        this.userContext = userContext;
    }
    
    @Override
    public boolean isCanceled() {
        return canceled;
    }
    
    /**
     * Sets the canceled flag on the task so that the processor will know
     * to stop working on this task, returns true if the task was canceled
     * before the task was complete.
     * @return Returns true when cancel was effective (task was not already complete)
     */
    public boolean cancel() {
        canceled = true;
        return !isComplete();
    }
    
    public void addSuccess() {
        this.successCount++;
        this.completedItems++;
    }
    
    public void addFailed() {
        this.failedCount++;
        this.completedItems++;
    }
    
    public void addUnsupported() {
        this.unsupportedCount++;
        this.completedItems++;
    }
    
    /** Most recent first comparison */
    @Override
    public int compareTo(AbstractInventoryTask o) {
        long s1 = getStartedAt();
        long s2 = o.getStartedAt();
        
        return -Long.compare(s1, s2);
    }
}