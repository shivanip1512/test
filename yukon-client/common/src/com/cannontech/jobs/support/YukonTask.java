package com.cannontech.jobs.support;

import com.cannontech.jobs.model.YukonJob;

public interface YukonTask {
    public void start();
    public void stop() throws UnsupportedOperationException;
    public void setJob(YukonJob job);
}
