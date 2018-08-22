package com.cannontech.common.device.groups;

public class IllegalGroupNameException extends IllegalArgumentException {

    private static final String defaultMsg = "group name may not be blank, contain '/' or '\\', nor exceed 60 characters: ";

    public IllegalGroupNameException(String groupName) {
        super(defaultMsg + groupName);
    }

    public IllegalGroupNameException(String groupName, Throwable cause) {
        super(defaultMsg + groupName, cause);
    }
}
