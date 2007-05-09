/**
 * 
 */
package com.cannontech.cbc.exceptions;

import com.cannontech.database.TransactionException;

/**
 * @author ekhazon
 *
 */
public class SerialNumberExistsException extends TransactionException {

	/**
	 * 
	 */
	public SerialNumberExistsException() {
		super();
	}

	/**
	 * @param s
	 */
	public SerialNumberExistsException(String s) {
		super(CBCExceptionMessages.MSG_DUPLICATE_SERIAL_NUMBER + s);
	}

	/**
	 * @param s
	 * @param t
	 */
	public SerialNumberExistsException(String s, Throwable t) {
		super(s, t);
	}



}
