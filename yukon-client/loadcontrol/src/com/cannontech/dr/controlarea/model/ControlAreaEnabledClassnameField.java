package com.cannontech.dr.controlarea.model;

import com.cannontech.messaging.message.loadcontrol.data.ControlAreaItem;
import com.cannontech.user.YukonUserContext;

public class ControlAreaEnabledClassnameField extends ControlAreaBackingFieldBase {

    @Override
    public String getFieldName() {
        return "ENABLED_CLASSNAME";
    }

    @Override
    public Object getControlAreaValue(ControlAreaItem controlArea,
            YukonUserContext userContext) {
        return controlArea.getDisableFlag() ? "drDisabled" : "drEnabled";
    }

    @Override
    protected boolean handlesNull() {
        return false;
    }
}
