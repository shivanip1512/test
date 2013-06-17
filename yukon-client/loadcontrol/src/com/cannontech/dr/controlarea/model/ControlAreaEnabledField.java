package com.cannontech.dr.controlarea.model;

import com.cannontech.loadcontrol.data.LMControlArea;
import com.cannontech.user.YukonUserContext;

public class ControlAreaEnabledField extends ControlAreaBackingFieldBase {

    @Override
    public String getFieldName() {
        return "ENABLED";
    }

    @Override
    public Object getControlAreaValue(LMControlArea controlArea,
            YukonUserContext userContext) {
        if (controlArea == null) {
            return "";
        }
        return buildResolvable(controlArea.getDisableFlag() ? "DISABLED" : "ENABLED");
    }

    @Override
    protected boolean handlesNull() {
        return true;
    }
}
