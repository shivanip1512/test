package com.cannontech.core.roleproperties;

public class RoleGroupNotInRoleException extends NotInRoleException {
    public RoleGroupNotInRoleException(YukonRoleProperty requestedProperty, Integer roleGroupId) {
        super("Role group with the id" + roleGroupId + " must be in " + requestedProperty.getRole()+ " to access " + requestedProperty + " property");
    }
}
