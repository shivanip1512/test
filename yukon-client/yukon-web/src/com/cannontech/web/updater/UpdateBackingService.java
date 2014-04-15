package com.cannontech.web.updater;

import com.cannontech.user.YukonUserContext;

public interface UpdateBackingService {
    /**
     * @param identifier
     * @param afterDate the if-modified-since date, or null if the latest value is desired
     * @return null if unchanged
     */
    String getLatestValue(String identifier, long afterDate, YukonUserContext userContext);

    boolean isValueAvailableImmediately(String fullIdentifier, long afterDate, YukonUserContext userContext);
}
