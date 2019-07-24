package com.cannontech.common.dr.setup;

import com.cannontech.common.i18n.DisplayableEnum;

public enum LmSetupFilterType implements DisplayableEnum {

    CONTROL_AREA,
    CONTROL_SCENARIO,
    LOAD_GROUP,
    LOAD_PROGRAM,
    MACRO_LOAD_GROUP,
    PROGRAM_CONSTRAINT;

    @Override
    public String getFormatKey() {
        return "yukon.web.modules.dr.setup." + name();
    }

}
