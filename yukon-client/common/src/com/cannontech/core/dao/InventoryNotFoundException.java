package com.cannontech.core.dao;

public class InventoryNotFoundException extends NotFoundException {

	public InventoryNotFoundException(String message) {
		super(message);
	}
	
	public InventoryNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

}
