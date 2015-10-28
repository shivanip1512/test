package com.cannontech.common.exception;

/**
 * Used to indicate that authentication failed. Specifically designed to not track
 * a cause or a message that would indicate why authentication failed.
 */
public class BadAuthenticationException extends Exception {
    private final String messageKey = "yukon.web.error.csrf.badPassword";
    public static enum Type {
        UNKNOWN,
        INCORRECT,
        DISABLED
    }
    private Type type = Type.UNKNOWN;
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
