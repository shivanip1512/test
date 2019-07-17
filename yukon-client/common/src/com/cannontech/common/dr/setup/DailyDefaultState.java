package com.cannontech.common.dr.setup;

import com.cannontech.common.i18n.DisplayableEnum;

public enum DailyDefaultState implements DisplayableEnum {
    None, 
    Enabled, 
    Disabled;

    @Override
    public String getFormatKey() {
        return "yukon.web.modules.dr.setup.controlArea." + name();
    }
}
