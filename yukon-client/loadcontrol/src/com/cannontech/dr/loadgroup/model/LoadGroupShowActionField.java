package com.cannontech.dr.loadgroup.model;

import com.cannontech.loadcontrol.data.LMDirectGroupBase;
import com.cannontech.user.YukonUserContext;

public class LoadGroupShowActionField extends LoadGroupBackingFieldBase {

    @Override
    public String getFieldName() {
        return "SHOW_ACTION";
    }
    
    @Override
    public Object getGroupValue(LMDirectGroupBase group, YukonUserContext userContext) {
        
        // Check null first - load management doesn't know about this group
        if (group == null) {
            return "unknown";
        }

        // Check enabled
        if (group.getDisableFlag()) {
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
