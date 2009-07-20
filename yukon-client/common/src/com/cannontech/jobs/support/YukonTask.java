package com.cannontech.jobs.support;

import com.cannontech.user.YukonUserContext;

public interface YukonTask {
    public void start(int jobId);
    public void stop(int jobId) throws UnsupportedOperationException;
    public void setUserContext(YukonUserContext userContext);
}
