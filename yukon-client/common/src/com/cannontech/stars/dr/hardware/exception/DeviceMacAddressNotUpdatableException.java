package com.cannontech.stars.dr.hardware.exception;

import com.cannontech.stars.util.StarsClientRequestException;

public class DeviceMacAddressNotUpdatableException extends StarsClientRequestException {

    public DeviceMacAddressNotUpdatableException() {
        super("MAC Address cannot be updated");
    }

    public DeviceMacAddressNotUpdatableException(String message) {
        super(message);
    }

    public DeviceMacAddressNotUpdatableException(String message, Throwable cause) {
        super(message, cause);
    }
}
