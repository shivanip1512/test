package com.cannontech.web.bulk.model;

/** Exception thrown when there is a problem creating a Device Collection */
public class DeviceCollectionCreationException extends RuntimeException {

    public DeviceCollectionCreationException(String message) {
        super(message);
    }

    public DeviceCollectionCreationException(String message, Throwable cause) {
        super(message, cause);
    }
}
