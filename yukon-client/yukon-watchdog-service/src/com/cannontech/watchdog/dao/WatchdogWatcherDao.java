package com.cannontech.watchdog.dao;

import com.cannontech.common.pao.PaoClass;

public interface WatchdogWatcherDao {

    /**
     * Checks if the passed paoClass pao's exists in database.
     */
    boolean paoClassPaoExists(PaoClass paoClass);
    
    /**
     * Get Pao Id of gateway which was added last
     */
    int getIdForLatestGateway();
    
}
