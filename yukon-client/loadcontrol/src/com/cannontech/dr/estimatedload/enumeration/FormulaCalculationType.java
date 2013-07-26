package com.cannontech.dr.estimatedload.enumeration;

import com.cannontech.common.i18n.DisplayableEnum;

public enum FormulaCalculationType implements DisplayableEnum {

    FUNCTION,
    LOOKUP,
    ;

    private final String baseKey = "yukon.dr.estimatedLoad.calculationType.";
    
    @Override
    public String getFormatKey() {
        return baseKey + name();
    }
}
