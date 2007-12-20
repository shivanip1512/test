package com.cannontech.core.dynamic.exception;

public class DynamicDataAccessException extends RuntimeException {
    public DynamicDataAccessException(String message) {
        super(message);
    }
    public DynamicDataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
