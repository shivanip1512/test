package com.cannontech.common.alert.model;

import com.cannontech.common.i18n.DisplayableEnum;

public enum AlertType implements DisplayableEnum {

    ALARM,
    CAPCONTROL_SERVER_RESPONSE,
    GROUP_COMMAND_COMPLETION,
    GROUP_METER_READ_COMPLETION,
    OUTAGE_PROCESSING_READ_LOGS_COMPLETION,
    TAMPER_FLAG_PROCESSING_READ_INTERNAL_FLAGS_COMPLETION,
    DISCONNECT_COMPLETION;
    ;
    
    private final static String keyPrefix = "yukon.web.alerts.types.";

    public String getFormatKey() {
        return keyPrefix + name();
    }


}
