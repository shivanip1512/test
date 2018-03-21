package com.cannontech.common.bulk.collection.device.model;

import java.util.Date;

import org.joda.time.Instant;

import com.cannontech.common.device.commands.CommandRequestExecutionStatus;

/**
 * Used to display table on recent results page.
 */
public class CollectionActionFilteredResult {
    private CollectionAction action;
    private Instant startTime;
    private Instant stopTime;
    private CommandRequestExecutionStatus status;
    private int successCount;
    private int failureCount;
    private int notAttemptedCount;
    private int cacheKey;
    private String userName;
    
    public CollectionAction getAction() {
        return action;
    }
    public void setAction(CollectionAction action) {
        this.action = action;
    }
    public Instant getStartTime() {
        return startTime;
    }
    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }
    public Instant getStopTime() {
        return stopTime;
    }
    public void setStopTime(Instant stopTime) {
        this.stopTime = stopTime;
    }
    public int getSuccessCount() {
        return successCount;
    }
    public void setSuccessCount(int successCount) {
        this.successCount = successCount;
    }
    public int getFailureCount() {
        return failureCount;
    }
    public void setFailureCount(int failureCount) {
        this.failureCount = failureCount;
    }
    public int getCacheKey() {
        return cacheKey;
    }
    public void setCacheKey(int cacheKey) {
        this.cacheKey = cacheKey;
    }
    public CommandRequestExecutionStatus getStatus() {
        return status;
    }
    public void setStatus(CommandRequestExecutionStatus status) {
        this.status = status;
    }
    public int getNotAttemptedCount() {
        return notAttemptedCount;
    }
    public void setNotAttemptedCount(int notAttemptedCount) {
        this.notAttemptedCount = notAttemptedCount;
    }
    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    
    public Date getFormattedStartTime(){
        return startTime.toDate();
    }
    
    public Date getFormattedStopTime(){
        return stopTime.toDate();
    }
}
