package com.cannontech.jobs.support;

import com.cannontech.jobs.model.JobContext;

public abstract class YukonTaskBase implements YukonTask {
    
    private JobContext jobContext;
    
    public JobContext getJobContext() {
        return jobContext;
    }
    
    @Override
    public void setJobContext(JobContext jobContext) {
        this.jobContext = jobContext;
    }
    
    @Override
    public void stop() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Cannot stop this task.");
    }
}
