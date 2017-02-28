package com.cannontech.system;

import com.cannontech.common.i18n.DisplayableEnum;

public enum DREncryption implements DisplayableEnum {

    HONEYWELL;

    private final static String baseKey = "yukon.common.drEncryption.";
    
    @Override
    public String getFormatKey() {
        return baseKey + name();
    }
}
