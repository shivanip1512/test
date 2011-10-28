package com.cannontech.thirdparty.service;

import java.util.Map;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.thirdparty.digi.service.errors.ZigbeePingResponse;
import com.cannontech.thirdparty.model.ZigbeeDevice;

public interface ZigbeeStateUpdaterService {
    /**
     * Determines the status of all Gateways and updates their connection state.
     * @return 
     *  
     */
    public Map<PaoIdentifier, ZigbeePingResponse> updateAllGatewayStatuses();
    
    /**
     * Determines the status of all EndPoints and updates their connection state.
     * @return
     */
    
    public Map<PaoIdentifier,ZigbeePingResponse> updateAllEndPointStatuses();
    /**
     * Determines the status of the Gateway specified and updates the connection state.
     *  
     * @param endPoint
     * @return 
     */
    public ZigbeePingResponse updateGatewayStatus(ZigbeeDevice Gateway);

    /**
     * Determines the status of the End Point specified and updates the connection state.
     * 
     * @param endPoint
     * @return
     */
    public ZigbeePingResponse updateEndPointStatus(ZigbeeDevice endPoint);
    
    /**
     * Activate smart polling on device.
     * 
     */
    public void activateSmartPolling(ZigbeeDevice device);
}
