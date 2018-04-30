package com.cannontech.watchdogs.impl;

import com.cannontech.watchdog.base.YukonServices;

public interface ServiceStatusWatchdog {
    
    /**
     * Will return if a yukon service is running or not.
     * This can be used by other watchdogs to check if the service is running.
     */
    boolean isServiceRunning(YukonServices service);

}
