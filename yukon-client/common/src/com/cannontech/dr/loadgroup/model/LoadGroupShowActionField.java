package com.cannontech.dr.loadgroup.model;

import org.springframework.beans.factory.annotation.Autowired;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.loadcontrol.data.LMDirectGroupBase;
import com.cannontech.user.YukonUserContext;

public class LoadGroupShowActionField extends LoadGroupBackingFieldBase {

    @Autowired
    private RolePropertyDao rolePropertyDao;
    
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
            if(rolePropertyDao.checkProperty(YukonRoleProperty.ALLOW_LOAD_GROUP_CONTROL, userContext.getYukonUser())) {
                return "enabled";
            }
            else {
                return "disabled";
            }            
        }
    }
    
    @Override
    protected boolean handlesNull() {
        return true;
    }

}
