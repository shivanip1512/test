package com.cannontech.web.stars.service;

import java.util.List;

import com.cannontech.web.stars.gateway.model.WiFiMeterCommData;

public interface RfnWiFiCommDataService {

    /**
     * Returns WiFiMeterCommData Objects for a list of virtual gateway Ids;
     * @param virtualGatewayIds - A list of VirtualGatewayIds
     */
    List<WiFiMeterCommData> getWiFiMeterCommDataForVirtualGateway(List<Integer> virtualGatewayIds);

}
