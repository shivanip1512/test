package com.cannontech.common.alert.model;

import com.cannontech.common.i18n.DisplayableEnum;

public enum AlertType implements DisplayableEnum {

    ALARM,
    CAPCONTROL_SERVER_RESPONSE;
    
    private final static String keyPrefix = "yukon.web.alerts.types.";

    public String getFormatKey() {
        return keyPrefix + name();
    }


}
