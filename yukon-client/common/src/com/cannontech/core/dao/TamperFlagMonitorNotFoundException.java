package com.cannontech.core.dao;

public class TamperFlagMonitorNotFoundException extends NotFoundException {

	public TamperFlagMonitorNotFoundException() {
		super("Tamper Flag Monitor Does Not Exist.");
	}
	
	public TamperFlagMonitorNotFoundException(String message) {
		super(message);
	}
	
	public TamperFlagMonitorNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}
}
