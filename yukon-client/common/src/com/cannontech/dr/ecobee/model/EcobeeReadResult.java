package com.cannontech.dr.ecobee.model;

import java.io.File;

import org.joda.time.Instant;

import com.cannontech.common.util.Completable;

public class EcobeeReadResult implements Completable {
    
    private int totalReads;
    private File file;
    private final Instant startDate;
    private Instant endDate;
    private int completedReads;
    private boolean isComplete;
    
    public EcobeeReadResult(int totalReads, File file) {
        this.totalReads = totalReads;
        this.file = file;
        startDate = Instant.now();
    }
    
    public File getFile() {
        return file;
    }
    
    public void setFile(File file) {
        this.file = file;
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
    
    public void setComplete(boolean isComplete) {
        this.isComplete = isComplete;
        endDate = Instant.now();
    }
    
    private void setCompletion() {
        if (!isComplete && completedReads == totalReads) {
            endDate = Instant.now();
            isComplete = true;
        }
    }

}
