package com.cannontech.servlet;

/**
 * See com.cannontech.common.constants.LoginController for a list of parameters and usage info
 * TODO:  Move common login code to an external class, not everyone always uses an http interface
 * Creation date: (12/7/99 9:46:12 AM)
 * @author:	Aaron Lauinger 
 */

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.cannontech.clientutils.ActivityLogger;
import com.cannontech.database.TransactionException;
import com.cannontech.database.cache.functions.AuthFuncs;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.roles.application.WebClientRole;

public class LoginController extends javax.servlet.http.HttpServlet {
	
	// These should be moved into global type properties
	// so we can change them at runtime FIXFIX 
	private static final String INVALID_URI = "/login.jsp?failed=true";
	private static final String INVALID_PARAMS = "failed=true";
	private static final String LOGIN_URI = "/login.jsp";
		
	private static final String ACTION = com.cannontech.common.constants.LoginController.ACTION;

	private static final String LOGIN = com.cannontech.common.constants.LoginController.LOGIN;
	private static final String CLIENT_LOGIN = com.cannontech.common.constants.LoginController.CLIENT_LOGIN;
	private static final String LOGOUT = com.cannontech.common.constants.LoginController.LOGOUT;
	
	private static final String USERNAME = com.cannontech.common.constants.LoginController.USERNAME;
	private static final String PASSWORD = com.cannontech.common.constants.LoginController.PASSWORD;
	
	private static final String REDIRECT = com.cannontech.common.constants.LoginController.REDIRECT;
	private static final String SAVE_CURRENT_USER = com.cannontech.common.constants.LoginController.SAVE_CURRENT_USER;
	
	private static final String YUKON_USER = com.cannontech.common.constants.LoginController.YUKON_USER;
	private static final String SAVED_YUKON_USERS = com.cannontech.common.constants.LoginController.SAVED_YUKON_USERS;
		
	private static final String LOGIN_URL_COOKIE = com.cannontech.common.constants.LoginController.LOGIN_URL_COOKIE;
	
	private static final String LOGIN_WEB_ACTIVITY_ACTION = com.cannontech.database.data.activity.ActivityLogActions.LOGIN_WEB_ACTIVITY_ACTION;
	private static final String LOGIN_CLIENT_ACTIVITY_ACTION = com.cannontech.database.data.activity.ActivityLogActions.LOGIN_CLIENT_ACTIVITY_ACTION;
	private static final String LOGOUT_ACTIVITY_LOG = com.cannontech.database.data.activity.ActivityLogActions.LOGOUT_ACTIVITY_LOG;
	private static final String LOGIN_FAILED_ACTIVITY_LOG = com.cannontech.database.data.activity.ActivityLogActions.LOGIN_FAILED_ACTIVITY_LOG;
	
/**
 * Handles login authentication, logout.
  * Creation date: (5/5/00 10:48:46 AM)
 * @param req javax.servlet.http.HttpServletRequest
 * @param resp javax.servlet.http.HttpServletResponse
 * @exception javax.servlet.ServletException The exception description.
 * @exception java.io.IOException The exception description.
 */
public void service(HttpServletRequest req, HttpServletResponse resp) throws javax.servlet.ServletException, java.io.IOException 
{	
	String action = req.getParameter(ACTION).toString();
	String redirectURI = req.getParameter(REDIRECT);
	String referer = req.getHeader("referer");
	
	if(LOGIN.equalsIgnoreCase(action)) {
		String username = req.getParameter(USERNAME);
		String password = req.getParameter(PASSWORD);
		LiteYukonUser user = AuthFuncs.login(username,password);
		String home_url = null;
		
		if(user != null && 
			(home_url = AuthFuncs.getRolePropertyValue(user,WebClientRole.HOME_URL)) != null) {
			HttpSession session = req.getSession(true);
			
			try {
				if (req.getParameter(SAVE_CURRENT_USER) != null) {
					// Add the current user to the end of the saved user list
					LiteYukonUser currentUser = (LiteYukonUser) session.getAttribute( YUKON_USER );
					if (currentUser != null) { 
						java.util.LinkedList savedUsers = (java.util.LinkedList) session.getAttribute( SAVED_YUKON_USERS );
						if (savedUsers == null) {
							savedUsers = new java.util.LinkedList();
							session.setAttribute( SAVED_YUKON_USERS, savedUsers );
						}
						savedUsers.addLast( currentUser );
					}
				}
				else {
					// If SAVED_CURRENT_USER is not specified, "forget" all the saved users
					session.removeAttribute( SAVED_YUKON_USERS );
				}
				
				initSession(user, session);
				ActivityLogger.logEvent(user.getUserID(), LOGIN_WEB_ACTIVITY_ACTION, "User " + user.getUsername() + " (userid=" + user.getUserID() + ") has logged in from " + req.getRemoteAddr());
				
				//stash a cookie that might tell us later where they log in at								
				String loginUrl = AuthFuncs.getRolePropertyValue(user, WebClientRole.LOG_IN_URL, LOGIN_URI);
				Cookie c = new Cookie(LOGIN_URL_COOKIE, loginUrl);
				c.setPath("/"+req.getContextPath());
				c.setMaxAge(Integer.MAX_VALUE);
				resp.addCookie(c);
				
			} catch(TransactionException e) {
				session.invalidate();
				resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			} 

			resp.sendRedirect(req.getContextPath() + home_url);
		}
		else {  // Login failed, send them on their way using one of
				// REDIRECT parameter, referer, INVALID_URI in that order 
			if (redirectURI == null) {
				if(referer != null) {
					redirectURI = referer;
					if(!redirectURI.endsWith(INVALID_PARAMS)) 
						redirectURI += "?" + INVALID_PARAMS;	
				}
				else {
					redirectURI = req.getContextPath() + INVALID_URI;
				}
			}
			ActivityLogger.logEvent(LOGIN_FAILED_ACTIVITY_LOG, "Login attempt as " + username + " failed from " + req.getRemoteAddr());
			resp.sendRedirect(redirectURI);
		}
	}
	else 
	if(LOGOUT.equalsIgnoreCase(action)) {
		HttpSession session = req.getSession();
		
		if(session != null) {
			LiteYukonUser user = (LiteYukonUser) session.getAttribute(YUKON_USER);			
			java.util.LinkedList savedUsers = (java.util.LinkedList) session.getAttribute(SAVED_YUKON_USERS);
			session.invalidate();
			
			if(user != null) {
				redirectURI = AuthFuncs.getRolePropertyValue(user, WebClientRole.LOG_IN_URL);
				ActivityLogger.logEvent(user.getUserID(),LOGOUT_ACTIVITY_LOG, "User " + user.getUsername() + " (userid=" + user.getUserID() + ") has logged out from " + req.getRemoteAddr());
			}
			
			if (savedUsers != null) {
				// Restore saved yukon user into session after logging off
				LiteYukonUser savedUser = (LiteYukonUser) savedUsers.removeLast();
				session = req.getSession( true );
				session.setAttribute(YUKON_USER, savedUser);
				if (savedUsers.size() > 0)
					session.setAttribute(SAVED_YUKON_USERS, savedUsers);
			}
		}
		
		//Try to send them back to where they logged in from
		if (redirectURI == null) {
			Cookie[] cookies = req.getCookies();
			if(cookies != null) {		
				for(int i = 0; i < cookies.length; i++) {
					Cookie c = cookies[i];
					if(c.getName().equalsIgnoreCase(LOGIN_URL_COOKIE)) {
						redirectURI = c.getValue();
						break;
					}
				}
			}
		}
		
		if (redirectURI == null)
			redirectURI = LOGIN_URI;
		
		if (redirectURI.startsWith("/"))
			redirectURI = req.getContextPath() + redirectURI; 
		
		resp.sendRedirect(redirectURI);
	} 
	else 
	if(CLIENT_LOGIN.equalsIgnoreCase(action)){
		String username = req.getParameter(USERNAME);
		String password = req.getParameter(PASSWORD);	
		LiteYukonUser user = AuthFuncs.login(username,password);
		
		if(user != null) {
			HttpSession session = req.getSession(true);
			try {						
				initSession(user, session);
				ActivityLogger.logEvent(user.getUserID(), LOGIN_CLIENT_ACTIVITY_ACTION, "User " + user.getUsername() + " (userid=" + user.getUserID() + ") has logged in from " + req.getRemoteAddr());
			} catch(TransactionException e) {
				session.invalidate();
				resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			}
		}
		else{
			resp.sendError(HttpServletResponse.SC_FORBIDDEN);
		}
		
	}
	else {
		resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	}	
}

private void initSession(LiteYukonUser user, HttpSession session) throws TransactionException {
	session.setAttribute(YUKON_USER, user);
}

}
