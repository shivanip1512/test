package com.cannontech.core.dao;

public class GearNotFoundException extends NotFoundException {

	public GearNotFoundException(String message) {
        super(message);
    }

    public GearNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
