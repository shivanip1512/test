package com.cannontech.common.exception;

public class PasswordExpiredException extends Exception {
    private final String messageKey = "yukon.web.login.passwordExpired";

    public PasswordExpiredException(String message) {
        super(message);
    }
    
    public PasswordExpiredException() {
        super();
    }
    
    public String getMessageKey() {
        return messageKey;
    }
}
