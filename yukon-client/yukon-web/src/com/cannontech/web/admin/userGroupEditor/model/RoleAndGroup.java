package com.cannontech.web.admin.userGroupEditor.model;

import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.database.data.lite.LiteYukonGroup;

public final class RoleAndGroup {
    
    private YukonRole role;
    private LiteYukonGroup group;
    
    private RoleAndGroup(YukonRole role, LiteYukonGroup group) {
        this.role = role;
        this.group = group;
    }
    
    public static RoleAndGroup of(YukonRole role, LiteYukonGroup group) {
        return new RoleAndGroup(role, group);
    }
    
    public YukonRole getRole() {
        return role;
    }
    
    public LiteYukonGroup getGroup() {
        return group;
    }
    
}