package com.cannontech.dr.program.model;

import com.cannontech.loadcontrol.data.LMProgramBase;
import com.cannontech.user.YukonUserContext;

public class ProgramEnabledField extends ProgramBackingFieldBase {

    @Override
    public String getFieldName() {
        return "ENABLED";
    }

    @Override
    public Object getProgramValue(LMProgramBase program,
            YukonUserContext userContext) {
        if (program == null) {
            return "";
        }
        return buildResolvable(program.getDisableFlag() ? "DISABLED" : "ENABLED");
    }

    @Override
    protected boolean handlesNull() {
        return true;
    }
}
