/*
 * Created on Jun 23, 2003
 */
package com.cannontech.common.constants;

/**	
 * Constants for the parameters passed to com.cannontech.servlet.LoginController
 * 
 * LoginController takes a username/password pair and attempts to find a matching
 * Yukon user in the database.  What it does after that depends on the ACTION 
 * passed to it.
 * 
 * required parameters:
 * USERNAME 	
 * PASSWORD
 * ACTION
 * 
 * One of the following actions is required:
 * LOGIN
 * LOGOUT
 * RETRIEVE_PROPERTIES
 * 
 * Odd Ducks:
 * REDIRECT - Currently only has an effect on a LOGOUT, indicates where to redirect the browser after logging out 
 * 
 * @author aaron
  */
public interface LoginController {
	/* Parameter Names */
	public static final String USERNAME = "USERNAME";
	public static final String PASSWORD = "PASSWORD";
	public static final String ACTION = "ACTION";

	/* Possible Action Values */
	public static final String LOGIN = "LOGIN";
	public static final String CLIENT_LOGIN = "CLIENTLOGIN";
	public static final String LOGOUT = "LOGOUT";
	
	public static final String REDIRECT = "REDIRECT";
	
	public static final String YUKON_USER = "YUKON_USER";
	public static final String SAVED_YUKON_USER = "SAVED_YUKON_USER";
}
