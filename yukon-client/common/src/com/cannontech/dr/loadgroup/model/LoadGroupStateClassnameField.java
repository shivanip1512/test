package com.cannontech.dr.loadgroup.model;

import com.cannontech.loadcontrol.data.LMDirectGroupBase;
import com.cannontech.user.YukonUserContext;

public class LoadGroupStateClassnameField extends LoadGroupBackingFieldBase {

    @Override
    public String getFieldName() {
        return "STATE_CLASSNAME";
    }

    @Override
    public Object getGroupValue(LMDirectGroupBase group, YukonUserContext userContext) {
        LoadGroupState state = LoadGroupState.valueOf(group.getGroupControlState());
        return "LOADGROUP_STATE_" + state.toString();
    }

    @Override
    protected boolean handlesNull() {
        return false;
    }
}
