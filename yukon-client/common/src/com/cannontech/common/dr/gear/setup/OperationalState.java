package com.cannontech.common.dr.gear.setup;

import com.cannontech.common.i18n.DisplayableEnum;

public enum OperationalState implements DisplayableEnum {
    Automatic,
    ManualOnly,
    Timed;

    private String baseKey = "yukon.web.modules.dr.setup.gear.";

    @Override
    public String getFormatKey() {
        return baseKey + name();
    }

}
