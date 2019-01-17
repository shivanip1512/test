package com.cannontech.dr.itron.simulator.model;

import com.cannontech.common.i18n.DisplayableEnum;

public enum ItronBasicError implements DisplayableEnum {
    GENERIC,
    FATAL_ERROR,
    AUTHORIZATION_FAILURE,
    ;

    @Override
    public String getFormatKey() {
        return "yukon.web.modules.dr.itron.basicError." + name();

    }  
}
