package com.cannontech.common.device.creation;

public class DeviceCreationException extends RuntimeException {

    public DeviceCreationException(String message) {
        super(message);
    }

    public DeviceCreationException(String message, Throwable cause) {
        super(message, cause);
    }
}
