package com.cannontech.core.dao;

public class OutageMonitorNotFoundException extends NotFoundException {

	public OutageMonitorNotFoundException() {
		super("Outage Monitor Does Not Exist.");
	}
	
	public OutageMonitorNotFoundException(String message) {
		super(message);
	}
	
	public OutageMonitorNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}
}
