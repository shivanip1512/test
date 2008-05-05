package com.cannontech.stars.dr.program.model;

import com.cannontech.common.i18n.DisplayableEnum;

public enum ChanceOfControl implements DisplayableEnum {
    NONE,
    LIKELY,
    UNLIKELY;

    private static final String keyPrefix = "yukon.dr.program.displayname.chanceOfControl.";
    
    @Override
    public String getFormatKey() {
        return keyPrefix + name();
    }
    
    public static ChanceOfControl valueOfName(String name) {
        if ("(none)".equals(name)) return NONE;
        if (LIKELY.name().equalsIgnoreCase(name)) return LIKELY;
        if (UNLIKELY.name().equalsIgnoreCase(name)) return UNLIKELY;
        throw new IllegalArgumentException("No enum with name " + name + " found");
    }
    
}
