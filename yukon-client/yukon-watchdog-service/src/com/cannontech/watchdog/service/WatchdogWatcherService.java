package com.cannontech.watchdog.service;

import com.cannontech.watchdog.base.YukonServices;

public interface WatchdogWatcherService {

    /**
     * Identify paoClass of the passed service.
     * Make a dao call to check if pao exists for that pao class.
     * i.e on a DR system, cap control object will not exists and capcontrol service need not be checked.
     */
    boolean isServiceRequired(YukonServices serviceName);
}
