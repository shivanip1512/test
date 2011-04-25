package com.cannontech.amr.phaseDetect.data;

import com.cannontech.common.i18n.DisplayableEnum;

public enum Phase implements DisplayableEnum {
    A,
    B,
    C,;

    private final static String baseKey = "yukon.web.modules.amr.phases."; 
    
    @Override
    public String getFormatKey() {
        return baseKey + this;
    }
}