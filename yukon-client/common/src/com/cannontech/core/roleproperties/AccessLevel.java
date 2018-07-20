package com.cannontech.core.roleproperties;

public enum AccessLevel {

    ADMIN(1), 
    OWNER(2), 
    LIMITED(3), 
    ;
    
    int level;
    AccessLevel(int level) {
        this.level = level;
    }
    
    /**
     * Returns true if the user access level is less or equal the minimum level.
     * Examples:
     * If the user level is ADMIN , the user is allowed access to OWNER and LIMITED.
     * 
     * If the user level is OWNER and minLevel is LIMITED, OWNER(2) <=  LIMITED(3) = true, the user is allowed access.
     * If the user level is LIMITED  and minLevel is OWNER, LIMITED(3) <= OWNER(2) = false, the user is not allowed access.
     */
    public boolean grantAccess(AccessLevel minLevel) {
        return this.level <= minLevel.level;
    }
}
