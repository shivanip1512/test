package com.cannontech.common.pao.model;

import com.cannontech.common.i18n.DisplayableEnum;

public enum DistanceUnit implements DisplayableEnum{

    KILOMETERS,
    MILES,
    NAUTICAL_MILES,
    ;

    @Override
    public String getFormatKey() {
        return "yukon.common.distance." + name();
    }
    
}