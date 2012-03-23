/**
 * 
 */
package com.cannontech.selenium.core.input;

/**
 * Use this class to report any issues related to invalid data from input files.
 * @author anuradha.uduwage
 *
 */
public class InvalidDataException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	/**
	 * @param message
	 */
	public InvalidDataException(String message) {
		super(message);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public InvalidDataException(String message, Exception cause) {
		super(message, cause);
	}

}
