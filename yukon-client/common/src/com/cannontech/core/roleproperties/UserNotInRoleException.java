package com.cannontech.core.roleproperties;

import com.cannontech.database.data.lite.LiteYukonUser;


public class UserNotInRoleException extends RuntimeException {
    public UserNotInRoleException(YukonRoleProperty requestedProperty, LiteYukonUser user) {
        super("User " + user + " must be in " + requestedProperty.getRole()+ " to access " + requestedProperty + " property");
    }
}
