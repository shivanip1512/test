package com.cannontech.core.roleproperties;


public class UserNotInRoleException extends RuntimeException {
    public UserNotInRoleException(YukonRoleProperty requestedProperty) {
        super("User must be in " + requestedProperty.getRole()+ " to access " + requestedProperty + " property");
    }
}
