package com.cannontech.database.cache.functions;

import com.cannontech.database.data.lite.LiteYukonUser;

public class UnknownRolePropertyException extends Exception {
    public UnknownRolePropertyException(LiteYukonUser user, int rolePropertyID) {
        super("Unknown RoleProperty " + rolePropertyID + " for user " + user);
    }
}
