package com.cannontech.cbc.exceptions;


public class PAODoesntHaveNameException extends Exception {

	public PAODoesntHaveNameException() {
		super(CBCExceptionMessages.MSG_NONAME_PAO);
	}

}
