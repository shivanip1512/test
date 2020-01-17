package com.cannontech.web.stars.service;

import java.util.List;

import com.cannontech.web.stars.gateway.model.WiFiMeterCommData;

public interface RfnWiFiCommDataService {

    /**
     * @return WiFiMeterCommData Objects for a list of gateway Ids;
     * @param gatewayIds - A list of GatewayIds
     */
    List<WiFiMeterCommData> getWiFiMeterCommDataForGateways(List<Integer> gatewayIds);

}
