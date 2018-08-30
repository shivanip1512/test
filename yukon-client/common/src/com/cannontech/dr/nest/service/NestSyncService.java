package com.cannontech.dr.nest.service;

import com.cannontech.dr.nest.model.NestSyncTimeInfo;

public interface NestSyncService {

    /**
     * Attempts to sync Yukon and Nest
     * Details are not available yet
     */
    void sync();

    /**
     * Returns the sync time information
     */
    NestSyncTimeInfo getSyncTimeInfo();

}
