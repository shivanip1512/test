package com.cannontech.web.updater;

import java.util.Set;

import com.cannontech.user.YukonUserContext;

public interface DataUpdaterService {
    
    public UpdateResponse getUpdates(Set<String> tokens, long afterDate, YukonUserContext userContext);
    public UpdateValue getFirstValue(String identifier, YukonUserContext userContext);
}
