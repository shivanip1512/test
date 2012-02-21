package com.cannontech.thirdparty.digi.service.impl;

import com.cannontech.thirdparty.service.ZigbeeWebService;


public class YukonZigbeeWebServiceFactory {

    private ZigbeeWebService zigbeeWebService;
    
    public ZigbeeWebService getZigbeeWebService() throws Exception {
        return zigbeeWebService;
    }

    public void setZigbeeWebService(ZigbeeWebService zigbeeWebService) {
        this.zigbeeWebService = zigbeeWebService;
    }
}
