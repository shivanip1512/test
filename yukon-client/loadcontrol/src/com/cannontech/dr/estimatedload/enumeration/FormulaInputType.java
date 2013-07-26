package com.cannontech.dr.estimatedload.enumeration;

import com.cannontech.common.i18n.DisplayableEnum;

public enum FormulaInputType implements DisplayableEnum {

    TEMP,
    HUMIDITY,
    TIME,
    CONTROL_PERCENT,
    RAMP_RATE,
    POINT,
    ;

    private final String baseKey = "yukon.dr.estimatedLoad.inputType."; 

    @Override
    public String getFormatKey() {
        return baseKey + name();
    }
}
