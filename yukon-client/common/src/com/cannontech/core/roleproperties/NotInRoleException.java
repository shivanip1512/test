package com.cannontech.core.roleproperties;

public class NotInRoleException extends RuntimeException {
    public NotInRoleException(String string) {
        super(string);
    }
}