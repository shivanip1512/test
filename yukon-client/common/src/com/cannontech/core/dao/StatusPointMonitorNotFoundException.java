package com.cannontech.core.dao;

public class StatusPointMonitorNotFoundException extends NotFoundException {

	public StatusPointMonitorNotFoundException() {
		super("Status Point Monitor Does Not Exist.");
	}
	
	public StatusPointMonitorNotFoundException(String message) {
		super(message);
	}
	
	public StatusPointMonitorNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}
}
