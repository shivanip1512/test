package com.cannontech.common.dr.setup;

import com.cannontech.common.i18n.DisplayableEnum;

public enum LmSetupFilterType implements DisplayableEnum {

    // TODO: Remove the commented enums later.
    /*CONTROL_AREA,
    CONTROL_SECNARIO,
    GEAR,*/
    LOAD_GROUP,
    LOAD_PROGRAM;
    /*MACRO_LOAD_GROUP;*/

    @Override
    public String getFormatKey() {
        return "yukon.web.modules.dr.setup." + name();
    }

}
