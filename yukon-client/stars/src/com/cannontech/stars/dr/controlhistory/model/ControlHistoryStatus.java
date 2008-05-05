package com.cannontech.stars.dr.controlhistory.model;

import com.cannontech.common.i18n.DisplayableEnum;

public enum ControlHistoryStatus implements DisplayableEnum {
    NOT_ENROLLED,
    OPTED_OUT,
    CONTROLLED_TODAY,
    CONTROLLED_CURRENT,
    CONTROLLED_NONE;
    
    private static final String keyPrefix = "yukon.dr.program.displayname.controlHistoryStatus.";

    @Override
    public String getFormatKey() {
        return keyPrefix + name();
    }
    
}
