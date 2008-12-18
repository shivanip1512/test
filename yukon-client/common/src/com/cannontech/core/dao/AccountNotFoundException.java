package com.cannontech.core.dao;

public class AccountNotFoundException extends NotFoundException {

	public AccountNotFoundException(String message) {
		super(message);
	}
	
	public AccountNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

}
