package com.cannontech.core.authorization.exception;

/**
 * Thrown when a user tries to perform an operation on a pao that they do not
 * have permission for.
 */
public class PaoAuthorizationException extends Exception {

    // Permission that has been violated
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
