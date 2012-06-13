package com.cannontech.common.exception;

/**
 * Used to indicate that authentication failed. Specifically designed to not track
 * a cause or a message that would indicate why authentication failed.
 */
public class BadAuthenticationException extends Exception {
    private final String messageKey = "yukon.web.error.csrf.badPassword";
    
    public BadAuthenticationException() {
        super();
    }
    
    public String getMessageKey() {
        return messageKey;
    }
}
