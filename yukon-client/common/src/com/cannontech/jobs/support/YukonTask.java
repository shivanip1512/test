package com.cannontech.jobs.support;

import com.cannontech.jobs.model.JobContext;

public interface YukonTask {
    public void start();
    public void stop() throws UnsupportedOperationException;
    public void setJobContext(JobContext jobContext);
}
