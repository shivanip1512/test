package com.cannontech.common.bulk.mapper;

import com.cannontech.common.bulk.processor.ProcessorCallbackException;
import com.cannontech.common.device.creation.DeviceCreationException;
import com.cannontech.common.exception.DisplayableRuntimeException;

public class ObjectMappingException extends DisplayableRuntimeException implements ProcessorCallbackException {

    public ObjectMappingException(String message) {
        super(message);
    }

    public ObjectMappingException(String message, Throwable cause) {
        super(message, cause);
    }

    public ObjectMappingException(DeviceCreationException e) {
        super(e.getMessage(), e.getKey(), e.getArgs());
    }
}