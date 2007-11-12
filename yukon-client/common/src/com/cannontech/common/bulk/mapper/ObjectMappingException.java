package com.cannontech.common.bulk.mapper;

/**
 * Exception thrown when there is a problem when trying to map an object
 */
public class ObjectMappingException extends RuntimeException {

    public ObjectMappingException(String message) {
        super(message);
    }

    public ObjectMappingException(String message, Throwable cause) {
        super(message, cause);
    }
}
