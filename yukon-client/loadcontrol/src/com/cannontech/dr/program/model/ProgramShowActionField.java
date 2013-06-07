package com.cannontech.dr.program.model;

import com.cannontech.messaging.message.loadcontrol.data.Program;
import com.cannontech.user.YukonUserContext;

public class ProgramShowActionField extends ProgramBackingFieldBase {

    @Override
    public String getFieldName() {
        return "SHOW_ACTION";
    }
    
    @Override
    public Object getProgramValue(Program program, YukonUserContext userContext) {
        
        // Check null first - load management doesn't know about this group
        if (program == null) {
            return "unknown";
        }

        // Check manual active
        int programState = program.getProgramStatus();
        boolean running = programState == Program.STATUS_MANUAL_ACTIVE
            || programState == Program.STATUS_TIMED_ACTIVE;
        boolean scheduled = programState == Program.STATUS_SCHEDULED;
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
