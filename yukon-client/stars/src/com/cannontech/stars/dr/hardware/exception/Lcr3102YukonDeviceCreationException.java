package com.cannontech.stars.dr.hardware.exception;

import com.cannontech.stars.util.StarsClientRequestException;

public class Lcr3102YukonDeviceCreationException extends StarsClientRequestException {
    
    public static enum Type {
        UNKNOWN,
        REQUIRED,
        UNAVAILABLE,
        NON_NUMERIC
    }
    
    private Type type = Type.UNKNOWN;
    
    public Lcr3102YukonDeviceCreationException(String message) {
        super(message);
    }
    
    public Lcr3102YukonDeviceCreationException(String message, Throwable cause) {
        super(message, cause);
    }
    
	public Lcr3102YukonDeviceCreationException(Throwable cause) {
	    super("Unable to create a Yukon device to assign to this Two Way LCR.", cause);
	}

    public Lcr3102YukonDeviceCreationException(Type type) {
        this.type = type;
    }

    public Lcr3102YukonDeviceCreationException(Throwable cause, Type type) {
        super("Unable to create a Yukon device to assign to this Two Way LCR.", cause);
        this.type = type;
    }

    public Type getType() {
        return type;
    }
    
}