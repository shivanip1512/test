package com.cannontech.watchdog.service;

import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.watchdog.base.YukonServices;

public interface WatchdogWatcherService {

    /**
     * Identify if service is required or not based on pao class/ pao type 
     * Calls method to find if pao exists.
     * i.e on a DR system, cap control object will not exists and capcontrol service need not be checked.
     */
    boolean isServiceRequired(YukonServices serviceName);
    
    /**
     * Return a RFN identifier for a gateway.
     */
    RfnIdentifier getGatewayRfnIdentifier() throws NotFoundException;
}
