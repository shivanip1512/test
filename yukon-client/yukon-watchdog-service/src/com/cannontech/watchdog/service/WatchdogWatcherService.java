package com.cannontech.watchdog.service;

import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.watchdog.base.YukonServices;

public interface WatchdogWatcherService {

    /**
     * Identify paoClass of the passed service.
     * Make a dao call to check if pao exists for that pao class.
     * i.e on a DR system, cap control object will not exists and capcontrol service need not be checked.
     */
    boolean isServiceRequired(YukonServices serviceName);
    
    /**
     * Return a RFN identifier for a gateway.
     */
    RfnIdentifier getGatewayRfnIdentifier() throws NotFoundException;
}
