package com.cannontech.dr.program.model;

import com.cannontech.loadcontrol.data.LMProgramBase;
import com.cannontech.user.YukonUserContext;

public class ProgramShowActionField extends ProgramBackingFieldBase {

    @Override
    public String getFieldName() {
        return "SHOW_ACTION";
    }
    
    @Override
    public Object getProgramValue(LMProgramBase program, YukonUserContext userContext) {
        
        // Check null first - load management doesn't know about this group
        if (program == null) {
            return "unknown";
        }

        // Check manual active
        boolean manualActive = program.getProgramStatus() == LMProgramBase.STATUS_MANUAL_ACTIVE;
        boolean disabled = program.getDisableFlag();

        if (manualActive && disabled) {
            return "runningDisabled";
        } else if (manualActive && !disabled) {
            return "runningEnabled";
        } else if(disabled) {
            return "disabled";
        } else {
            return "enabled";
        }
    }
    
    @Override
    protected boolean handlesNull() {
        return true;
    }
}
