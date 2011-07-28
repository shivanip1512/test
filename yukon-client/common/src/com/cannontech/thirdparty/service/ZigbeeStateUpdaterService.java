package com.cannontech.thirdparty.service;

import com.cannontech.thirdparty.model.ZigbeeDevice;

public interface ZigbeeStateUpdaterService {
    /**
     * Determines the status of all Gateways and updates their connection state.
     *  
     */
    public void updateAllGatewayStatuses();
    
    /**
     * Determines the status of the Gateway specified and updates the connection state.
     *  
     * @param endPoint
     */
    public void updateGatewayStatus(ZigbeeDevice Gateway);

    /**
     * Determines the status of the End Point specified and updates the connection state.
     *  
     * @param endPoint
     */
    public void updateEndPointStatus(ZigbeeDevice endPoint);
}
