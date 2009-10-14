package com.cannontech.dr.controlarea.model;

import com.cannontech.loadcontrol.data.LMControlArea;
import com.cannontech.user.YukonUserContext;

public class ControlAreaStateClassnameField extends ControlAreaBackingFieldBase {

    @Override
    public String getFieldName() {
        return "STATE_CLASSNAME";
    }
    
    @Override
    public Object getControlAreaValue(LMControlArea controlArea, YukonUserContext userContext) {
        return "";
    }
    
    @Override
    protected boolean handlesNull() {
        return true;
    }

}
