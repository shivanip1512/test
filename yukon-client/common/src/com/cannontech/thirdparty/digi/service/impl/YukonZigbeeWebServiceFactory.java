package com.cannontech.thirdparty.digi.service.impl;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.thirdparty.service.ZigbeeWebService;


public class YukonZigbeeWebServiceFactory {

    private ConfigurationSource configurationSource;

    private ZigbeeWebService zigbeeWebService;
    private ZigbeeWebService dummyWebService = new DummyZigbeeWebServiceImpl();
    
    private boolean digiEnabled;
    
    public ZigbeeWebService getZigbeeWebService() throws Exception {
        if (digiEnabled) {
            return zigbeeWebService;
        }
        return dummyWebService;
    }

    @PostConstruct
    public void setup() {
        digiEnabled = configurationSource.getBoolean("DIGI_ENABLED", false);      
    }

    public void setZigbeeWebService(ZigbeeWebService zigbeeWebService) {
        this.zigbeeWebService = zigbeeWebService;
    }
    
    @Autowired
    public void setConfigurationSource(ConfigurationSource configurationSource) {
        this.configurationSource = configurationSource;
    }
}
