package com.cannontech.dr.rfn.model;

import com.cannontech.common.i18n.DisplayableEnum;

public enum UnknownStatus implements DisplayableEnum {
    
    ACTIVE,
    INACTIVE,
    UNAVAILABLE,
    UNREPORTED_NEW,
    UNREPORTED_OLD;

    @Override
    public String getFormatKey() {
        return "yukon.common.rfPerformance.unknownStatus." + name();
    }
}