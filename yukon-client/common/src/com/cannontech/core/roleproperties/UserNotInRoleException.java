package com.cannontech.core.roleproperties;

public class UserNotInRoleException extends NotInRoleException {
    public UserNotInRoleException(YukonRoleProperty requestedProperty, Integer userId) {
        super("User with the id " + userId + " must be in " + requestedProperty.getRole()+ " to access " + requestedProperty + " property");
    }
}
