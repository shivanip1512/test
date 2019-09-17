package com.cannontech.core.roleproperties;

public enum HierarchyPermissionLevel {

    OWNER(1), //access to everything
    CREATE(2), // access to CREATE, UPDATE, LIMITED 
    UPDATE(3), // access to UPDATE, LIMITED 
    LIMITED(4), // access to LIMITED (adds a few interactions on top of View mode)
    RESTRICTED(5), // view
    NO_ACCESS(6);   // no view
    
    int level;
    HierarchyPermissionLevel(int level) {
        this.level = level;
    }
    
    /**
     * Returns true if the user access level is less or equal the minimum level.
     * Examples:
     * If the user level is UPDATE , the user is allowed access to UPDATE, LIMITED and RESTRICTED.
     * 
     * If the user level is UPDATE and minLevel is LIMITED, UPDATE(3) <=  LIMITED(4) = true, the user is allowed access.
     * If the user level is LIMITED  and minLevel is UPDATE, LIMITED(4) <= UPDATE(3) = false, the user is not allowed access.
     */
    public boolean grantAccess(HierarchyPermissionLevel minLevel) {
        return this.level <= minLevel.level;
    }
}
