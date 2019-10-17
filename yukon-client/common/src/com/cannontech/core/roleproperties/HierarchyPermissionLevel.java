package com.cannontech.core.roleproperties;

import com.cannontech.common.i18n.DisplayableEnum;

public enum HierarchyPermissionLevel implements DisplayableEnum {

    OWNER(1), //access to everything
    CREATE(2), // access to CREATE, UPDATE, LIMITED 
    UPDATE(3), // access to UPDATE, LIMITED 
    INTERACT(4), // access to LIMITED (adds a few interactions on top of View mode)
    VIEW(5), // view
    NO_ACCESS(6);   // no view
    
    private int level;
    private HierarchyPermissionLevel(int level) {
        this.level = level;
    }
    
    /**
     * Returns true if the user access level is less or equal the minimum level.
     * Examples:
     * If the user level is UPDATE , the user is allowed access to UPDATE, INTERACT and VIEW.
     * 
     * If the user level is UPDATE and minLevel is INTERACT, UPDATE(3) <=  INTERACT(4) = true, the user is allowed access.
     * If the user level is INTERACT  and minLevel is UPDATE, INTERACT(4) <= UPDATE(3) = false, the user is not allowed access.
     */
    public boolean grantAccess(HierarchyPermissionLevel minLevel) {
        return this.level <= minLevel.level;
    }
    
    public int getLevel() {
        return level;
    }
    
    @Override
    public String getFormatKey() {
        return "yukon.common.roleproperty.HierarchyPermissionLevel." + name();
    }
}
