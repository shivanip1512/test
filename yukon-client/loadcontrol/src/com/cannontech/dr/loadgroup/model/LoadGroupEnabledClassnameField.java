package com.cannontech.dr.loadgroup.model;

import com.cannontech.loadcontrol.data.LMDirectGroupBase;
import com.cannontech.user.YukonUserContext;

public class LoadGroupEnabledClassnameField extends LoadGroupBackingFieldBase {

    @Override
    public String getFieldName() {
        return "ENABLED_CLASSNAME";
    }

    @Override
    public Object getGroupValue(LMDirectGroupBase loadGroup,
            YukonUserContext userContext) {
        return loadGroup.getDisableFlag() ? "drDisabled" : "drEnabled";
    }

    @Override
    protected boolean handlesNull() {
        return false;
    }
}
