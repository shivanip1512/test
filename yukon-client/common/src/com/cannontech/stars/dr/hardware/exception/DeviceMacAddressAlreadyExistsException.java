package com.cannontech.stars.dr.hardware.exception;

import com.cannontech.stars.util.StarsClientRequestException;

public class DeviceMacAddressAlreadyExistsException extends StarsClientRequestException {

    public DeviceMacAddressAlreadyExistsException() {
        super("A MAC Address already exists for a device");
    }

    public DeviceMacAddressAlreadyExistsException(String message) {
        super(message);
    }

    public DeviceMacAddressAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
