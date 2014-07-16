package com.cannontech.web.stars.gateway.model;

import com.cannontech.common.i18n.DisplayableEnum;

public enum ConnectStatus implements DisplayableEnum {
    
    CONNECTED, DISCONNECTED;
    
    @Override
    public String getFormatKey() {
        return "yukon.common.gateways.connectStatus." + name();
    }
}