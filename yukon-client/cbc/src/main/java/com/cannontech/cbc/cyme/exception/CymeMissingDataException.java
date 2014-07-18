package com.cannontech.cbc.cyme.exception;

public class CymeMissingDataException  extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

	public CymeMissingDataException(String message) {
        super(message);
    }
}
