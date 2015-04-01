package com.cannontech.common.util;

public class ResultExpiredException extends RuntimeException {
    
    private static final String defaultMsg = "The result has expired and is no longer available: ";

    public ResultExpiredException(String message) {
        super(defaultMsg + message);
    }

    public ResultExpiredException(String message, Throwable cause) {
        super(defaultMsg + message, cause);
    }
}