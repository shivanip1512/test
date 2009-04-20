package com.cannontech.common.exception;

public class StarsNotCreatedException extends RuntimeException {

	public StarsNotCreatedException(String message, Throwable cause) {
		super(message, cause);
	}

	public StarsNotCreatedException(String message) {
		super(message);
	}
}
