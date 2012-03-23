/**
 * 
 */
package com.cannontech.selenium.core.input;

import com.cannontech.selenium.core.SolventSeleniumException;

/**
 * @author anuradha.uduwage
 *
 */
public class MissingDataFileException extends SolventSeleniumException {

	private static final long serialVersionUID = 1L;
	
	public MissingDataFileException(String messString) {
		super(messString);
	}
}
