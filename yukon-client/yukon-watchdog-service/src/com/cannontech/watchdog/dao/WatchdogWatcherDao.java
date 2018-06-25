package com.cannontech.watchdog.dao;

import com.cannontech.common.pao.PaoClass;
import com.cannontech.common.rfn.message.RfnIdentifier;

public interface WatchdogWatcherDao {

    /**
     * Checks if the passed paoClass pao's exists in database.
     */
    boolean paoClassPaoExists(PaoClass paoClass);
    
    /**
     * Get Rfn Identifier of gateway which was added last
     */
    RfnIdentifier getIdForLatestGateway();
    
}
