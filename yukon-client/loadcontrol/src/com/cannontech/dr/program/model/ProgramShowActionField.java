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
        int programState = program.getProgramStatus();
        boolean running = programState == LMProgramBase.STATUS_MANUAL_ACTIVE
            || programState == LMProgramBase.STATUS_TIMED_ACTIVE;
        boolean scheduled = programState == LMProgramBase.STATUS_SCHEDULED;
        boolean disabled = program.getDisableFlag();

        if (running && disabled) {
            return "runningDisabled";
        } else if (running && !disabled) {
            return "runningEnabled";
        } else if (scheduled && !disabled) {
            return "scheduledEnabled";
        } else if (scheduled && !disabled) {
            return "scheduledEnabled";
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
