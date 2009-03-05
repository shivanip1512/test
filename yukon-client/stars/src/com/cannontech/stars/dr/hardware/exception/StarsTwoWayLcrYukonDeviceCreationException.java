package com.cannontech.stars.dr.hardware.exception;

import com.cannontech.stars.util.StarsClientRequestException;

public class StarsTwoWayLcrYukonDeviceCreationException extends StarsClientRequestException {

	public StarsTwoWayLcrYukonDeviceCreationException() {
        super("Unable to create a Yukon device to assign to this Two Way LCR.");
    }

    public StarsTwoWayLcrYukonDeviceCreationException(String message) {
        super(message);
    }

    public StarsTwoWayLcrYukonDeviceCreationException(String message, Throwable cause) {
        super(message, cause);
    }
	
}
