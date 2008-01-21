package com.cannontech.jobs.support;

import com.cannontech.user.YukonUserContext;

public abstract class YukonTaskBase implements YukonTask {
    private YukonUserContext userContext;
    
    public void setUserContext(YukonUserContext userContext) {
        this.userContext = userContext;
    }
    public YukonUserContext getUserContext() {
        return userContext;
    }
}
