package com.cannontech.core.authorization.exception;

public class PaoAuthorizationException extends Exception {

    private String permission = null;

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public PaoAuthorizationException(String message) {
        super(message);
    }

    public PaoAuthorizationException(String message, String permission) {
        super(message);
        this.permission = permission;
    }
}
