package com.cannontech.web.updater;

import com.cannontech.user.YukonUserContext;

public interface SingleUpdateService {
    public String getLatestValue(long afterDate, YukonUserContext userContext);
}
