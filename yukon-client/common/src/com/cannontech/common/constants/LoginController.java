/*
 * Created on Jun 23, 2003
 */
package com.cannontech.common.constants;

import com.cannontech.util.ServletUtil;

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
 * REDIRECT - When used with a LOGIN, specifies where to redirect the brower
 * if login failed. When used with a LOGOUT, indicates where to redirect the
 * browser after logging out.
 * 
 * SAVE_CURRENT_USER - Only has an effect on a LOGIN, when specified, the
 * old session context will be saved under the SAVED_YUKON_USERS attribute
 * of the new session context. When the new user logs off, the old session
 * context will be restored.
 * 
 * @author aaron
  */
public interface LoginController {
	/* Parameter Names */
	public static final String USERNAME = "USERNAME";
	public static final String PASSWORD = "PASSWORD";
	public static final String ACTION = "ACTION";
	public static final String REDIRECT = ServletUtil.ATT_REDIRECT;
	public static final String SAVE_CURRENT_USER = "SAVE_CURRENT_USER";
	
	/* Possible Action Values */
	public static final String LOGIN = "LOGIN";
	public static final String CLIENT_LOGIN = "CLIENTLOGIN";
	public static final String LOGOUT = "LOGOUT";
	
	public static final String YUKON_USER = ServletUtil.ATT_YUKON_USER;
	public static final String SAVED_YUKON_USERS = "SAVED_YUKON_USERS";
	
	public static final String LOGIN_URL_COOKIE = "LOGIN_URL";
}
