package com.cannontech.stars.dr.hardware.exception;

import com.cannontech.stars.util.StarsClientRequestException;

public class StarsTwoWayLcrYukonDeviceCreationException extends StarsClientRequestException {
    
    public static enum Type {
        UNKNOWN,
        REQUIRED,
        UNAVAILABLE,
        NON_NUMERIC
    }
    
    private Type type = Type.UNKNOWN;
    
    public StarsTwoWayLcrYukonDeviceCreationException(String message) {
        super(message);
    }
    
    public StarsTwoWayLcrYukonDeviceCreationException(String message, Throwable cause) {
        super(message, cause);
    }
    
	public StarsTwoWayLcrYukonDeviceCreationException(Throwable cause) {
	    super("Unable to create a Yukon device to assign to this Two Way LCR.", cause);
	}

    public StarsTwoWayLcrYukonDeviceCreationException(Type type) {
        this.type = type;
    }

    public StarsTwoWayLcrYukonDeviceCreationException(Throwable cause, Type type) {
        super("Unable to create a Yukon device to assign to this Two Way LCR.", cause);
        this.type = type;
    }

    public Type getType() {
        return type;
    }
    
}