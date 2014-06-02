package com.cannontech.dr.ecobee.model;

import org.joda.time.Instant;

import com.cannontech.common.util.Completable;

public class EcobeeReadResult implements Completable {
    private final Instant startDate;
    private Instant endDate;
    private int totalReads;
    private int completedReads;
    private boolean isComplete;
    
    public EcobeeReadResult(int totalReads) {
        this.totalReads = totalReads;
        startDate = Instant.now();
    }

    public void incrementCompleted() {
        completedReads++;
        setCompletion();
    }
    
    public void addCompleted(int amount) {
        completedReads += amount;
        setCompletion();
    }
    
    public int getTotal() {
        return totalReads;
    }
    
    public int getCompleted() {
        return completedReads;
    }
    
    public Instant getStartDate() {
        return startDate;
    }
    
    public Instant getEndDate() {
        return endDate;
    }
    
    @Override
    public boolean isComplete() {
        return isComplete;
    }
    
    private void setCompletion() {
        if (!isComplete && completedReads == totalReads) {
            endDate = Instant.now();
            isComplete = true;
        }
    }
}
