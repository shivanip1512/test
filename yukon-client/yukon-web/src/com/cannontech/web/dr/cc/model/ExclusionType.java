package com.cannontech.web.dr.cc.model;

import com.cannontech.common.i18n.DisplayableEnum;

public enum ExclusionType implements DisplayableEnum {
    NO_DIRECT_PROGRAM,
    ECON_EVENT_COLLISION,
    EVENT_COLLISION,
    POINT_ERROR,
    EXCEEDS_ALLOWED_HOURS,
    EXCEEDS_ALLOWED_PERIOD_HOURS,
    SHORT_NOTICE,
    ISOC_POINTS;
    
    private static final String keyBase = "yukon.web.modules.dr.cc.init.customerVerification.exclusion.";
    
    @Override
    public String getFormatKey() {
        return keyBase + name();
    }
}