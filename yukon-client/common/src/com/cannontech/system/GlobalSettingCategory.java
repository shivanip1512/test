package com.cannontech.system;

import com.cannontech.common.i18n.DisplayableEnum;

public enum GlobalSettingCategory implements DisplayableEnum {
    
    APPLICATION,
    SYSTEM_SETUP,
    INTEGRATION,
    OTHER;

    @Override
    public String getFormatKey() {
        return "yukon.common.setting." + name();
    }
    
}