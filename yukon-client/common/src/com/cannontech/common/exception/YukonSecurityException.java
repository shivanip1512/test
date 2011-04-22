package com.cannontech.common.exception;

public class YukonSecurityException extends RuntimeException {
    private final String messageKey = "yukon.web.error.csrf.badToken";

    public YukonSecurityException(String message, Throwable cause) {
        super(message, cause);
    }

    public YukonSecurityException(String message) {
        super(message);
    }

    public String getMessageKey() {
        return messageKey;
    }
    
}
