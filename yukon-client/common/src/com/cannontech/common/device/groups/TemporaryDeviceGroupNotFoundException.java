package com.cannontech.common.device.groups;

import com.cannontech.core.dao.NotFoundException;

public class TemporaryDeviceGroupNotFoundException extends NotFoundException {
    
    private static final String defaultMsg = "The device group has expired and its contents are no longer available: ";

    public TemporaryDeviceGroupNotFoundException(String message) {
        super(defaultMsg + message);
    }

    public TemporaryDeviceGroupNotFoundException(String message, Throwable cause) {
        super(defaultMsg + message, cause);
    }
}