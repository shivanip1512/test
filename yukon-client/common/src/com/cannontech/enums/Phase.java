package com.cannontech.enums;

import com.cannontech.common.i18n.DisplayableEnum;

public enum Phase implements DisplayableEnum {
    A,
    B,
    C,;

    private final static String baseKey = "yukon.common.phase.phase"; 
    
    @Override
    public String getFormatKey() {
        return baseKey + this;
    }
}