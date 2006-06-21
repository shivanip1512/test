package com.cannontech.core.dao;

import com.cannontech.database.data.lite.LiteYukonUser;

public class UnknownRolePropertyException extends Exception {
    public UnknownRolePropertyException(LiteYukonUser user, int rolePropertyID) {
        super("Unknown RoleProperty " + rolePropertyID + " for user " + user);
    }
}
