package com.cannontech.common.exception;

public class YukonSecurityException extends RuntimeException {

    public YukonSecurityException(String message, Throwable cause) {
        super(message, cause);
    }

    public YukonSecurityException(String message) {
        super(message);
    }
    
}
