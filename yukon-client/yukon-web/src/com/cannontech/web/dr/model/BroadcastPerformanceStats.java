package com.cannontech.web.dr.model;

import java.text.DecimalFormat;

public class BroadcastPerformanceStats {
    
    private int success;
    private int failed;
    private int unknown;
    
    public BroadcastPerformanceStats(int success, int failed, int unknown) {
        this.success = success;
        this.failed = failed;
        this.unknown = unknown;
    }
    
    public int getSuccess() {
        return success;
    }
    
    public void setSuccess(int success) {
        this.success = success;
    }
    
    public int getFailed() {
        return failed;
    }
    
    public void setFailed(int failed) {
        this.failed = failed;
    }
    
    public int getUnknown() {
        return unknown;
    }
    
    public void setUnknown(int unknown) {
        this.unknown = unknown;
    }
    
    public String getSuccessPercent() {
        
        DecimalFormat format = new DecimalFormat("##0.#%");
        
        double percentComplete = ((double)success / (double) (success + failed));
        
        return format.format(percentComplete);
    }
}