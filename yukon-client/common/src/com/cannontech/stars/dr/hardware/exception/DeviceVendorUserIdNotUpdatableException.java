package com.cannontech.stars.dr.hardware.exception;

import com.cannontech.stars.util.StarsClientRequestException;

public class DeviceVendorUserIdNotUpdatableException extends StarsClientRequestException {

    public DeviceVendorUserIdNotUpdatableException() {
        super("Device Vendor User Id cannot be updated");
    }

    public DeviceVendorUserIdNotUpdatableException(String message) {
        super(message);
    }

    public DeviceVendorUserIdNotUpdatableException(String message, Throwable cause) {
        super(message, cause);
    }
}
