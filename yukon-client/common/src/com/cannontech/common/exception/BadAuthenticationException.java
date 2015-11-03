package com.cannontech.common.exception;

/**
 * Used to indicate that authentication failed. Specifically designed to not track
 * a cause or a message that would indicate why authentication failed.
 */
public class BadAuthenticationException extends Exception {
    private final String messageKey = "yukon.web.error.csrf.badPassword";
    public static enum Type {
        DISABLED_USER,
        UNKNOWN_USER,
        INVALID_PASSWORD
    }
    private Type type = Type.UNKNOWN_USER;
    public BadAuthenticationException() {
        super();
    }
    
    public BadAuthenticationException(Type type) {
        this.type = type;
    }
    
    public Type getType() {
        return type;
    }
    public String getMessageKey() {
        return messageKey;
    }
}
