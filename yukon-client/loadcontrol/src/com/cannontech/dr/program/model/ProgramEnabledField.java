package com.cannontech.dr.program.model;

import com.cannontech.messaging.message.loadcontrol.data.Program;
import com.cannontech.user.YukonUserContext;

public class ProgramEnabledField extends ProgramBackingFieldBase {

    @Override
    public String getFieldName() {
        return "ENABLED";
    }

    @Override
    public Object getProgramValue(Program program,
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
