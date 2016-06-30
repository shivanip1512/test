package com.cannontech.common.bulk.mapper;

import com.cannontech.common.bulk.processor.ProcessorCallbackException;
import com.cannontech.common.device.creation.DeviceCreationException;
import com.cannontech.common.exception.DisplayableRuntimeException;

public class ObjectMappingException extends DisplayableRuntimeException implements ProcessorCallbackException {

    private static final String keyBase = "yukon.exception.objectMappingException.";

    // Below 2 constructors are used where i'18 is not required 
    public ObjectMappingException(String message) {
        super(message);
    }

    public ObjectMappingException(String message, Throwable cause) {
        super(message);
    }

    public ObjectMappingException(String message, String key, String from, Throwable cause) {
        super(message, cause, keyBase + key, from);
    }

    public ObjectMappingException(String message, String key, String name) {
        super(message, keyBase + key, name);
    }

    public ObjectMappingException(String message, String key, Object value, Throwable cause) {
        super(message, cause, keyBase + key, value);
    }

    public ObjectMappingException(DeviceCreationException e) {
        super(e.getMessage(), e.getKey(), e.getArgs());
    }
}