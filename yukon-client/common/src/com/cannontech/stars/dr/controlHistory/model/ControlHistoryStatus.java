package com.cannontech.stars.dr.controlHistory.model;

import com.cannontech.common.i18n.DisplayableEnum;

public enum ControlHistoryStatus implements DisplayableEnum {
    NOT_ENROLLED,
    VIRTUALLY_ENROLLED,
    OPTED_OUT,
    CONTROLLED_PREVIOUSLY,
    CONTROLLED_TODAY,
    CONTROLLED_CURRENT,
    CONTROLLED_NONE;
    
    private static final String keyPrefix = "yukon.dr.program.displayname.controlHistoryStatus.";

    @Override
    public String getFormatKey() {
        return keyPrefix + name();
    }
    
}
