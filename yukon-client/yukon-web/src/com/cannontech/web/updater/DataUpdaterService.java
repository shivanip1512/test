package com.cannontech.web.updater;

import java.util.Set;

import com.cannontech.database.data.lite.LiteYukonUser;

public interface DataUpdaterService {
    
    public UpdateResponse getUpdates(Set<String> tokens, long afterDate, LiteYukonUser user);
    public UpdateValue getFirstValue(String identifier, LiteYukonUser user);
}
