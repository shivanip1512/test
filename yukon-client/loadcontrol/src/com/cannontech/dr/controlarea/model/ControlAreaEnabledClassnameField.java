package com.cannontech.dr.controlarea.model;

import com.cannontech.loadcontrol.data.LMControlArea;
import com.cannontech.user.YukonUserContext;

public class ControlAreaEnabledClassnameField extends ControlAreaBackingFieldBase {

    @Override
    public String getFieldName() {
        return "ENABLED_CLASSNAME";
    }

    @Override
    public Object getControlAreaValue(LMControlArea controlArea,
            YukonUserContext userContext) {
        return controlArea.getDisableFlag() ? "drDisabled" : "drEnabled";
    }

    @Override
    protected boolean handlesNull() {
        return false;
    }
}
