package com.cannontech.jobs.support;

import com.cannontech.user.YukonUserContext;

public interface YukonTask {
    public void start();
    public void stop() throws UnsupportedOperationException;
    public void setUserContext(YukonUserContext userContext);
}
