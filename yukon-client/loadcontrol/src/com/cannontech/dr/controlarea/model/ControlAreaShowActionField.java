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
        boolean noAssignedPrograms = controlArea.getLmProgramVector().isEmpty();
        boolean inactive = controlArea.getControlAreaState() == LMControlArea.STATE_INACTIVE;
        boolean fullyActive = controlArea.getControlAreaState() == LMControlArea.STATE_FULLY_ACTIVE;
        boolean disabled = controlArea.getDisableFlag();

        if (noAssignedPrograms) {
            return "noAssignedPrograms";
        } else if (fullyActive && disabled) {
            return "fullyActiveDisabled";
        } else if (fullyActive && !disabled) {
            return "fullyActiveEnabled";
        } else if (inactive && disabled) {
            return "inactiveDisabled";
        } else if (inactive && !disabled) {
            return "inactiveEnabled";
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
