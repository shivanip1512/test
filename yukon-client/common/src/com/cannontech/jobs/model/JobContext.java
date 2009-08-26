package com.cannontech.jobs.model;


public class JobContext {

    private YukonJob job;
    
    public JobContext(YukonJob job) {
        this.job = job;
    }
    
    public YukonJob getJob() {
        return job;
    }
    public void setJob(YukonJob job) {
        this.job = job;
    }
}
