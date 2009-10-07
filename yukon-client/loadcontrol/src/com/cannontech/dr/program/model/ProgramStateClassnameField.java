package com.cannontech.dr.program.model;

import com.cannontech.loadcontrol.data.LMProgramBase;
import com.cannontech.user.YukonUserContext;

public class ProgramStateClassnameField extends ProgramBackingFieldBase {

    @Override
    public String getFieldName() {
        return "STATE_CLASSNAME";
    }
    
    @Override
    public Object getProgramValue(LMProgramBase program, YukonUserContext userContext) {
        return "";
    }
    
    @Override
    protected boolean handleNull() {
        return true;
    }

}
