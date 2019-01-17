package com.cannontech.dr.itron.simulator.model;

import com.cannontech.common.i18n.DisplayableEnum;

public enum AddProgramError implements DisplayableEnum {
    GENERIC,
    FATAL_ERROR,
    AUTHORIZATION_FAILURE,
    UNIQUE_PROGRAM_PROGRAMNAME,
    SIZE_PROGRAM_PROGRAMNAME,
    NOTBLANK_PROGRAM_PROGRAMNAME
    ;

    @Override
    public String getFormatKey() {
        return "yukon.web.modules.dr.itron.addProgramError." + name();

    }  
}
