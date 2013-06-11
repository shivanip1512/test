package com.cannontech.common.util;

import com.cannontech.common.i18n.DisplayableEnum;

public enum TimeZoneFormat implements DisplayableEnum {
    LOCAL,
    LOCAL_NO_DST,
    UTC;
    
    private final static String keyPrefix = "yukon.common.";
    
    @Override
    public String getFormatKey() {
        return keyPrefix + "tzDisplayOption." + name();
    }
}
