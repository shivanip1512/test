package com.cannontech.dr.ecobee.model;

import java.io.File;
import java.text.DecimalFormat;

import org.joda.time.Instant;

import com.cannontech.common.util.Completable;

public class EcobeeReadResult implements Completable {
    
    private String key;
    private int totalReads;
    private File file;
    private final Instant startDate;
    private Instant endDate;
    private int completedReads;
    private boolean isComplete;
    private boolean successful;
    
    public EcobeeReadResult(int totalReads, File file) {
        this.totalReads = totalReads;
        this.file = file;
        startDate = Instant.now();
    }
    
    public String getKey() {
        return key;
    }
    
    public void setKey(String key) {
        this.key = key;
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
    
    public boolean isSuccessful() {
        return successful;
    }
    
    public void setSuccessful(boolean successful) {
        this.successful = successful;
    }
    
    private void setCompletion() {
        if (!isComplete && completedReads == totalReads) {
            endDate = Instant.now();
            isComplete = true;
        }
    }
    
    /**
     * Returns the percent done formatted as ###.#%
     */
    public String getPercentDone() {
        
        if (isComplete) {
            return "100%";
        }
        double percent = (double) completedReads / totalReads;
        
        return new DecimalFormat("###.#%").format(percent);
    }

}
