package com.cannontech.dr.controlarea.model;

import com.cannontech.loadcontrol.data.LMControlArea;
import com.cannontech.user.YukonUserContext;

public class ControlAreaShowActionField extends ControlAreaBackingFieldBase {

    @Override
    public String getFieldName() {
        return "SHOW_ACTION";
    }
    
    @Override
    public Object getControlAreaValue(LMControlArea controlArea, YukonUserContext userContext) {
        
        // Check null first - load management doesn't know about this group
        if (controlArea == null) {
            return "unknown";
        }

        // Check manual active
        boolean fullyActive = controlArea.getControlAreaState() == LMControlArea.STATE_FULLY_ACTIVE;
        boolean disabled = controlArea.getDisableFlag();

        if (fullyActive && disabled) {
            return "fullyActiveDisabled";
        } else if (fullyActive && !disabled) {
            return "fullyActiveEnabled";
        } else if(disabled) {
            return "disabled";
        } else {
            return "enabled";
        }
    }
    
    @Override
    protected boolean handleNull() {
        return true;
    }
}
