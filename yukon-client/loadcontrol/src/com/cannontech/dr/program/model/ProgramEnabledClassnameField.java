package com.cannontech.dr.program.model;

import com.cannontech.messaging.message.loadcontrol.data.Program;
import com.cannontech.user.YukonUserContext;

public class ProgramEnabledClassnameField extends ProgramBackingFieldBase {

    @Override
    public String getFieldName() {
        return "ENABLED_CLASSNAME";
    }

    @Override
    public Object getProgramValue(Program program,
            YukonUserContext userContext) {
        return program.getDisableFlag() ? "drDisabled" : "drEnabled";
    }

    @Override
    protected boolean handlesNull() {
        return false;
    }
}
