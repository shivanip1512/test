package com.cannontech.core.roleproperties;

import com.cannontech.common.i18n.DisplayableEnum;

public enum SerialNumberValidation implements DisplayableEnum {

    NUMERIC,
    ALPHANUMERIC;

    @Override
    public String getFormatKey() {
        return "yukon.common.serialNumberValidationEnum." + name();
    }
}