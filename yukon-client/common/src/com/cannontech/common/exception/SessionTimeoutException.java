package com.cannontech.common.exception;

public class SessionTimeoutException extends RuntimeException {
    
    private static final String defaultMsg = "Session Expired";

    public SessionTimeoutException() {
        super(defaultMsg);
    }
    
    public SessionTimeoutException(Throwable cause) {
        super(defaultMsg, cause);
    }
}