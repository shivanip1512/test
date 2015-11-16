package com.cannontech.web.stars.gateway.model;

import com.cannontech.common.i18n.DisplayableEnum;

public enum LastComm implements DisplayableEnum {
    
    SUCCESS, FAILURE, MISSED, UNKNOWN;

    @Override
    public String getFormatKey() {
        return "yukon.common.gateways.lastComm." + name();
    }
}