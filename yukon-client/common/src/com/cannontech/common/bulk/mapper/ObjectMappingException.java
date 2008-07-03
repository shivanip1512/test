package com.cannontech.common.bulk.mapper;

import com.cannontech.common.bulk.processor.ProcessorCallbackException;

public class ObjectMappingException extends RuntimeException implements ProcessorCallbackException {

    public ObjectMappingException(String message) {
        super(message);
    }

    public ObjectMappingException(String message, Throwable cause) {
        super(message, cause);
    }
}
