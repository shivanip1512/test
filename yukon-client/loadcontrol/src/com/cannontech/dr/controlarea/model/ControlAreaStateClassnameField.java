package com.cannontech.dr.controlarea.model;

import com.cannontech.messaging.message.loadcontrol.data.ControlAreaItem;
import com.cannontech.user.YukonUserContext;

public class ControlAreaStateClassnameField extends ControlAreaBackingFieldBase {

    @Override
    public String getFieldName() {
        return "STATE_CLASSNAME";
    }

    @Override
    public Object getControlAreaValue(ControlAreaItem controlArea, YukonUserContext userContext) {
        ControlAreaState state = ControlAreaState.valueOf(controlArea.getControlAreaState());
        return "CONTROLAREA_STATE_" + state.toString();
    }

    @Override
    protected boolean handlesNull() {
        return false;
    }
}
