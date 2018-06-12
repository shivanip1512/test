package com.cannontech.watchdog.dao;

import com.cannontech.watchdog.base.YukonServices;

public interface WatchdogWatcherDao {

    /**
     * Checks if the passed service is valid for the deployed system.
     * e.g. On a DR system, Capcontrol service need not be checked.
     */
    boolean isValidService(YukonServices serviceName);
}
