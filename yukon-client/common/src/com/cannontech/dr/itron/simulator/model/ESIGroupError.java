package com.cannontech.dr.itron.simulator.model;

import com.cannontech.common.i18n.DisplayableEnum;

public enum ESIGroupError implements DisplayableEnum {
    ERROR_MACID_NOT_FOUND,
    ERROR_MACID_INVALID
    ;

    @Override
    public String getFormatKey() {
        return "yukon.web.modules.dr.itron.esiGroupError." + name();

    }  
}
