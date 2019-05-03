package com.cannontech.common.alert.model;

import com.cannontech.common.i18n.DisplayableEnum;

public enum AlertType implements DisplayableEnum {

    ALARM,
    WARNING,
    CAPCONTROL_SERVER_RESPONSE,
    GROUP_COMMAND_COMPLETION,
    GROUP_METER_READ_COMPLETION,
    OUTAGE_PROCESSING_READ_LOGS_COMPLETION,
    TAMPER_FLAG_PROCESSING_READ_INTERNAL_FLAGS_COMPLETION,
    DISCONNECT_COMPLETION,
    DATA_STREAMING,
    DEMAND_RESET_COMPLETION,
    LOCATE_ROUTE,
    RFN_DEVICE_CREATION_FROM_TEMPLATE_FAILED,
    ;
    
    private final static String keyPrefix = "yukon.web.alerts.types.";

    @Override
    public String getFormatKey() {
        return keyPrefix + name();
    }


}
