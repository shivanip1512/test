package com.cannontech.dr.program.model;

import com.cannontech.messaging.message.loadcontrol.data.Program;
import com.cannontech.user.YukonUserContext;

public class ProgramStateClassnameField extends ProgramBackingFieldBase {

    @Override
    public String getFieldName() {
        return "STATE_CLASSNAME";
    }

    @Override
    public Object getProgramValue(Program program, YukonUserContext userContext) {
        ProgramState state = ProgramState.valueOf(program.getProgramStatus());
        return "PROGRAM_STATE_" + state.toString();
    }

    @Override
    protected boolean handlesNull() {
        return false;
    }
}
