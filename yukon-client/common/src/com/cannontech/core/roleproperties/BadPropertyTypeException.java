package com.cannontech.core.roleproperties;

public class BadPropertyTypeException extends RuntimeException {
    public BadPropertyTypeException(YukonRoleProperty property, String value, Throwable cause) {
        super("Unable to convert value of \"" + value + " for " + property, cause);
    }
}
