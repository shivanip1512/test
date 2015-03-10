package com.cannontech.dr.ecobee.model;

import java.io.File;
import java.text.DecimalFormat;

import org.joda.time.Instant;

import com.cannontech.common.util.Completable;
import com.cannontech.common.util.Range;

public class EcobeeReadResult implements Completable {
    
    private String key;
    private int totalReads;
    private File file;
    private final Instant startDate;
    private Instant endDate;
    private int completedReads;
    private boolean isComplete;
    private boolean successful;
    private Instant startDateRange;
    private Instant endDateRange;
    
    public EcobeeReadResult(int totalReads, File file, Range<Instant> dateRange) {
        this.totalReads = totalReads;
        this.file = file;
        startDate = Instant.now();
        startDateRange = dateRange.getMin();
        endDateRange = dateRange.getMax();
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
    
    public void setComplete() {
        if (!this.isComplete) {
            this.isComplete = true;
            endDate = Instant.now();
        }
    }
    
    public boolean isSuccessful() {
        return successful;
    }
    
    public void setSuccessful(boolean successful) {
        this.successful = successful;
    }
    
    public Instant getStartDateRange() {
        return startDateRange;
    }

    public void setStartDateRange(Instant startDateRange) {
        this.startDateRange = startDateRange;
    }

    public Instant getEndDateRange() {
        return endDateRange;
    }

    public void setEndDateRange(Instant endDateRange) {
        this.endDateRange = endDateRange;
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
