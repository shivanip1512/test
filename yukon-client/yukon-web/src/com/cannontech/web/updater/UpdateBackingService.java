package com.cannontech.web.updater;

import com.cannontech.database.data.lite.LiteYukonUser;


public interface UpdateBackingService {
    /**
     * @param identifier 
     * @param format the format string
     * @param afterDate the if-modified-since date, or null if the latest value is desired
     * @return null if unchanged
     */
    public String getLatestValue(String identifier, long afterDate, LiteYukonUser user);
}
