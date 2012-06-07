package com.cannontech.stars.dr.hardware.exception;

import com.cannontech.stars.util.StarsClientRequestException;

public class StarsTwoWayLcrYukonDeviceAssignmentException extends StarsClientRequestException {

	public StarsTwoWayLcrYukonDeviceAssignmentException() {
        super("Unable to assign the Yukon device to this Two Way LCR.");
    }

    public StarsTwoWayLcrYukonDeviceAssignmentException(String message) {
        super(message);
    }

    public StarsTwoWayLcrYukonDeviceAssignmentException(String message, Throwable cause) {
        super(message, cause);
    }
	
}