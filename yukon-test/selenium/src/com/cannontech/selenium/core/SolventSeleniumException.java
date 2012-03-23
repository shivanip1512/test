/**
 * 
 */
package com.cannontech.selenium.core;

import org.apache.log4j.Logger;

/**
 * @author anuradha.uduwage
 *
 */
public class SolventSeleniumException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(SolventSelenium.class.getName());
	
	public SolventSeleniumException(String stringMsg) {
		super(stringMsg);
		log.error(stringMsg);
	}
}
