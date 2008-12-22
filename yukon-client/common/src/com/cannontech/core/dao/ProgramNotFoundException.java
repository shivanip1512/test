package com.cannontech.core.dao;

public class ProgramNotFoundException extends NotFoundException {

	public ProgramNotFoundException(String message) {
		super(message);
	}
	
	public ProgramNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

}
