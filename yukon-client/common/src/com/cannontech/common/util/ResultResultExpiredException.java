package com.cannontech.common.util;

public class ResultResultExpiredException extends RuntimeException {
    
    private static final String defaultMsg = "The result has expired and is no longer available: ";

    public ResultResultExpiredException(String message) {
        super(defaultMsg + message);
    }

    public ResultResultExpiredException(String message, Throwable cause) {
        super(defaultMsg + message, cause);
    }
}