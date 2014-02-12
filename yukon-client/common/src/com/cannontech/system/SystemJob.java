package com.cannontech.system;

public enum SystemJob {
    
    RF_BROADCAST_PERFORMANCE(-1),
    RF_BROADCAST_PERFORMANCE_EMAIL(-2);
    
    private final int jobId;
    
    private SystemJob(int jobId) {
        this.jobId = jobId;
    }
    
    public int getJobId() {
        return jobId;
    }
    
}