package com.cannontech.servlet;

/**
 * See com.cannontech.common.constants.LoginController for a list of parameters and usage info
 * TODO:  Move common login code to an external class, not everyone always uses an http interface
 * Creation date: (12/7/99 9:46:12 AM)
 * @author:	Aaron Lauinger 
 */

import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.cannontech.clientutils.ActivityLogger;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.Pair;
import com.cannontech.database.TransactionException;
import com.cannontech.database.cache.functions.AuthFuncs;
import com.cannontech.database.cache.functions.ContactFuncs;
import com.cannontech.database.cache.functions.YukonUserFuncs;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.roles.application.WebClientRole;

public class LoginController extends javax.servlet.http.HttpServlet {
	
	// These should be moved into global type properties
	// so we can change them at runtime FIXFIX 
	private static final String INVALID_URI = "/login.jsp?failed=true";
	private static final String VOICE_ROOT = "/voice";
	private static final String INVALID_PARAMS = "failed=true";
	private static final String LOGIN_URI = "/login.jsp";
		
	private static final String ACTION = com.cannontech.common.constants.LoginController.ACTION;

	private static final String LOGIN = com.cannontech.common.constants.LoginController.LOGIN;
	private static final String CLIENT_LOGIN = com.cannontech.common.constants.LoginController.CLIENT_LOGIN;
	private static final String LOGOUT = com.cannontech.common.constants.LoginController.LOGOUT;
	private static final String VOICE_LOGIN = com.cannontech.common.constants.LoginController.VOICE_LOGIN;
	
	private static final String USERNAME = com.cannontech.common.constants.LoginController.USERNAME;
	private static final String PASSWORD = com.cannontech.common.constants.LoginController.PASSWORD;
	private static final String TOKEN = com.cannontech.common.constants.LoginController.TOKEN;
	
	private static final String REDIRECT = com.cannontech.common.constants.LoginController.REDIRECT;
	private static final String SAVE_CURRENT_USER = com.cannontech.common.constants.LoginController.SAVE_CURRENT_USER;
	
	private static final String YUKON_USER = com.cannontech.common.constants.LoginController.YUKON_USER;
	private static final String SAVED_YUKON_USERS = com.cannontech.common.constants.LoginController.SAVED_YUKON_USERS;
		
	private static final String LOGIN_URL_COOKIE = com.cannontech.common.constants.LoginController.LOGIN_URL_COOKIE;
	
	private static final String LOGIN_WEB_ACTIVITY_ACTION = com.cannontech.database.data.activity.ActivityLogActions.LOGIN_WEB_ACTIVITY_ACTION;
	private static final String LOGIN_CLIENT_ACTIVITY_ACTION = com.cannontech.database.data.activity.ActivityLogActions.LOGIN_CLIENT_ACTIVITY_ACTION;
	private static final String LOGOUT_ACTIVITY_LOG = com.cannontech.database.data.activity.ActivityLogActions.LOGOUT_ACTIVITY_LOG;
	private static final String LOGIN_FAILED_ACTIVITY_LOG = com.cannontech.database.data.activity.ActivityLogActions.LOGIN_FAILED_ACTIVITY_LOG;
	private static final String LOGIN_VOICE_ACTIVITY_ACTION = com.cannontech.database.data.activity.ActivityLogActions.LOGIN_VOICE_ACTIVITY_ACTION;
	
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
	/*this is unfortunate...can't take out this header call for now because
	 * we may not necessarily have a session available for getting the previous
	 * page since this code is hit both on a logoff and on a logon while the session is still null.  
	 * I'll at least make sure it isn't null where it is used in the code.
	 */
	String referer = req.getHeader("referer");
	
	if(LOGIN.equalsIgnoreCase(action)) {
		String username = req.getParameter(USERNAME);
		String password = req.getParameter(PASSWORD);
		LiteYukonUser user = AuthFuncs.login(username,password);
		String home_url = null;
		
		if(user != null && 
			(home_url = AuthFuncs.getRolePropertyValue(user,WebClientRole.HOME_URL)) != null) {
			HttpSession session = req.getSession();
			
			try {
				if (req.getParameter(SAVE_CURRENT_USER) != null) {
					if (session != null && session.getAttribute(YUKON_USER) != null) {
						Properties oldContext = new Properties();
						Enumeration attNames = session.getAttributeNames();
						while (attNames.hasMoreElements()) {
							String attName = (String) attNames.nextElement();
							oldContext.put( attName, session.getAttribute(attName) );
						}
						
						session.invalidate();
						session = req.getSession(true);
						
						if(referer == null)
							referer = AuthFuncs.getRolePropertyValue(user,WebClientRole.HOME_URL);
						
						// Save the old session context and where to direct the browser when the new user logs off
						session.setAttribute( SAVED_YUKON_USERS, new Pair(oldContext, referer) );
					}
				}
				else {
					if (session != null && session.getAttribute(YUKON_USER) != null) {
						session.invalidate();
						session = req.getSession(true);
					}
					
					//stash a cookie that might tell us later where they log in at								
					String loginUrl = AuthFuncs.getRolePropertyValue(user, WebClientRole.LOG_IN_URL, LOGIN_URI);
					if (loginUrl.startsWith("/")) loginUrl = req.getContextPath() + loginUrl;
					
					Cookie c = new Cookie(LOGIN_URL_COOKIE, loginUrl);
					c.setPath("/"+req.getContextPath());
					c.setMaxAge(Integer.MAX_VALUE);
					resp.addCookie(c);
				}
				
				initSession(user, session);
				ActivityLogger.logEvent(user.getUserID(), LOGIN_WEB_ACTIVITY_ACTION, "User " + user.getUsername() + " (userid=" + user.getUserID() + ") has logged in from " + req.getRemoteAddr());
				
			} catch(TransactionException e) {
				if (session != null) session.invalidate();
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
			Pair p = (Pair) session.getAttribute(SAVED_YUKON_USERS);
			session.invalidate();
			
			if(user != null) {
				redirectURI = AuthFuncs.getRolePropertyValue(user, WebClientRole.LOG_IN_URL);
				ActivityLogger.logEvent(user.getUserID(),LOGOUT_ACTIVITY_LOG, "User " + user.getUsername() + " (userid=" + user.getUserID() + ") has logged out from " + req.getRemoteAddr());
			}
			
			if (p != null) {
				Properties oldContext = (Properties) p.getFirst();
				redirectURI = (String) p.getSecond();
				
				// Restore saved session context
				session = req.getSession( true );
				Enumeration attNames = oldContext.propertyNames();
				while (attNames.hasMoreElements()) {
					String attName = (String) attNames.nextElement();
					session.setAttribute( attName, oldContext.get(attName) );
				}
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
			redirectURI = req.getContextPath() + LOGIN_URI;
		
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
	else if(VOICE_LOGIN.equalsIgnoreCase(action))
	{
		doVoiceLogin( req, resp, redirectURI, referer );
	}
	else {
		resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	}	
}

private static void initSession(LiteYukonUser user, HttpSession session) throws TransactionException {
	session.setAttribute(YUKON_USER, user);
}

/**
 * Used to login *internally* if you don't want to go through LoginController
 * (so that you can get around with Radius login, etc.).
 * Although this method checks for home_url, it won't redirect the user
 * to home_url. You're responsible for deciding where to send the user.
 * Notice that the session is invalidated if login succeed, you should
 * get a new session object after invoking this method.
 */
public static LiteYukonUser internalLogin(HttpServletRequest req, HttpSession session, String username, String password, boolean saveCurrentUser) {
	LiteYukonUser user = AuthFuncs.yukonLogin(username,password);
	if (user == null || AuthFuncs.getRolePropertyValue(user,WebClientRole.HOME_URL) == null)
		return null;
	
	Properties oldContext = null;
	if (saveCurrentUser && session.getAttribute(YUKON_USER) != null) {
		oldContext = new Properties();
		Enumeration attNames = session.getAttributeNames();
		while (attNames.hasMoreElements()) {
			String attName = (String) attNames.nextElement();
			oldContext.put( attName, session.getAttribute(attName) );
		}
	}
	
	session.invalidate();
	session = req.getSession( true );
	
	
	String referer = req.getHeader("referer");
	if(referer == null)
		referer = AuthFuncs.getRolePropertyValue(user,WebClientRole.HOME_URL);
	
	// Save the old session context and where to direct the browser when the new user logs off
	if (oldContext != null)
		session.setAttribute( SAVED_YUKON_USERS, new Pair(oldContext, referer) );
	
	try {
		initSession(user, session);
		ActivityLogger.logEvent(user.getUserID(), LOGIN_WEB_ACTIVITY_ACTION, "User " + user.getUsername() + " (userid=" + user.getUserID() + ") has logged in from " + req.getRemoteAddr());
	}
	catch (TransactionException e) {
		CTILogger.error( e.getMessage(), e );
	}
	
	return user;
}

/**
 * Handles the voice login process
 * 
 */
private void doVoiceLogin( HttpServletRequest req, HttpServletResponse resp, String redirectURI, String referer ) throws IOException
{
	String username = req.getParameter(USERNAME);  //contactid
	String password = req.getParameter(PASSWORD);  //pin	
		
	//if the pin is valid...log us in
	int contactid = Integer.MIN_VALUE;
	try { 
		contactid = Integer.parseInt(username);
	} catch(NumberFormatException nfe) { CTILogger.debug("Unable to parse given ContactID value into an Integer, value = " + contactid);}


	LiteContact lContact = ContactFuncs.getContact( contactid ); //store this for logging purposes only
	LiteYukonUser user = AuthFuncs.voiceLogin( contactid, password );

	String voice_home_url = "/voice/notification.jsp";
		
	if( user != null 
		/*&& (voice_home_url = AuthFuncs.getRolePropertyValue(user,WebClientRole.HOME_URL)) != null*/ )
	{
		HttpSession session = req.getSession();
			
		try {
			if (session != null && session.getAttribute(YUKON_USER) != null) {
				session.invalidate();
				session = req.getSession(true);
			}					
				
			initSession(user, session);
			session.setAttribute( TOKEN, req.getParameter(TOKEN) );
			ActivityLogger.logEvent(user.getUserID(), LOGIN_VOICE_ACTIVITY_ACTION, "VOICE User " + user.getUsername() + " (userid=" + user.getUserID() + ") (Contact=" + lContact.toString() + ") has logged in from " + req.getRemoteAddr());
				
		} catch(TransactionException e) {
			if (session != null) session.invalidate();
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}

		resp.sendRedirect(req.getContextPath() + voice_home_url);
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
				redirectURI = req.getContextPath() + VOICE_ROOT + INVALID_URI;
			}
		}
		ActivityLogger.logEvent(LOGIN_FAILED_ACTIVITY_LOG, "VOICE Login attempt for contact " + lContact.toString() + " failed from " + req.getRemoteAddr());
		resp.sendRedirect(redirectURI);
	}

}

}
