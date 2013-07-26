package com.cannontech.dr.estimatedload.enumeration;

import com.cannontech.common.i18n.DisplayableEnum;

public enum FormulaType implements DisplayableEnum {

    APPLIANCE_CATEGORY,
    GEAR,
    ;

    private final String baseKey = "yukon.dr.estimatedLoad.formulaType.";

    @Override
    public String getFormatKey() {
        return baseKey + name();
    }
}
