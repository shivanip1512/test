/**
 * 
 */
package com.cannontech.selenium.core;

/**
 * @author anuradha.uduwage
 *
 */
public class SolventConfigException extends RuntimeException {

	private static final long versionID = 1L;
	/**
	 * 
	 */
	public SolventConfigException() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 */
	public SolventConfigException(String message) {
		super(message);
	}
	
	/**
	 * 
	 * @param message message for the exception
	 * @param ex exception
	 */
	public SolventConfigException(String message, Exception ex) {
		super(message, ex);
	}

}
