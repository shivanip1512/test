package com.cannontech.system;

import com.cannontech.common.i18n.DisplayableEnum;

public enum KeyFileType implements DisplayableEnum {

    HONEYWELL;

    private final static String baseKey = "yukon.common.setting.";
    
    @Override
    public String getFormatKey() {
        return baseKey + name();
    }
}
