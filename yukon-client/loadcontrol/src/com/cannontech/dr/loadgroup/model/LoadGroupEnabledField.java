package com.cannontech.dr.loadgroup.model;

import com.cannontech.messaging.message.loadcontrol.data.DirectGroupBase;
import com.cannontech.user.YukonUserContext;

public class LoadGroupEnabledField extends LoadGroupBackingFieldBase {

    @Override
    public String getFieldName() {
        return "ENABLED";
    }

    @Override
    public Object getGroupValue(DirectGroupBase loadGroup,
            YukonUserContext userContext) {
        if (loadGroup == null) {
            return "";
        }
        return buildResolvable(loadGroup.getDisableFlag() ? "DISABLED" : "ENABLED");
    }

    @Override
    protected boolean handlesNull() {
        return true;
    }
}
