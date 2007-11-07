package com.cannontech.common.bulk.mapper;

/**
 * Exception thrown when this object was not mapped and should be ignored
 */
public class IgnoreMappingException extends Exception {

    public IgnoreMappingException(String message) {
        super(message);
    }

    public IgnoreMappingException(String message, Throwable cause) {
        super(message, cause);
    }
}
