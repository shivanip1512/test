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
	public static final String TOKEN = "TOKEN";

	public static final String YUKON_USER = ServletUtil.ATT_YUKON_USER;
	public static final String SAVED_YUKON_USERS = "SAVED_YUKON_USERS";

	public static final String FAILED_LOGIN_PARAM = "failed=true";
	public static final String INVALID_URL_ACCESS_PARAM = "invalid=true";
    public static final String AUTH_FAILED_PARAM = "failed";
    public static final String AUTH_RETRY_SECONDS_PARAM = "retrySeconds";

	public static final String LOGIN_URL = "/login.jsp";
    public static final String INVALID_URI = "/login.jsp?" + FAILED_LOGIN_PARAM;
	public static final String REMEMBER_ME_COOKIE = "REMEMBER_ME_COOKIE";
    public static final String REDIRECTED_FROM = "REDIRECTED_FROM";

}
