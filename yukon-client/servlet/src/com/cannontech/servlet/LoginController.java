package com.cannontech.servlet;

/**
 * See com.cannontech.common.constants.LoginController for a list of parameters and usage info
 * TODO: Pull out contstant strings HOME_URL and YUKON_USER 
 * 
 * Creation date: (12/7/99 9:46:12 AM)
 * @author:	Aaron Lauinger 
 */

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.TransactionException;
import com.cannontech.database.cache.functions.AuthFuncs;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.roles.application.WebClientRole;

public class LoginController extends javax.servlet.http.HttpServlet {
	
	// These should be moved into global type properties
	// so we can change them at runtime FIXFIX 
	private static final String INVALID_URI = "/login.jsp?failed=true";
	private static final String LOGIN_URI = "/login.jsp";
		
	private static final String ACTION = com.cannontech.common.constants.LoginController.ACTION;

	private static final String LOGIN = com.cannontech.common.constants.LoginController.LOGIN;
	private static final String CLIENT_LOGIN = com.cannontech.common.constants.LoginController.CLIENT_LOGIN;
	private static final String LOGOUT = com.cannontech.common.constants.LoginController.LOGOUT;
	
	private static final String USERNAME = com.cannontech.common.constants.LoginController.USERNAME;
	private static final String PASSWORD = com.cannontech.common.constants.LoginController.PASSWORD;
	
	private static final String REDIRECT = com.cannontech.common.constants.LoginController.REDIRECT;
	
	private static final String YUKON_USER = com.cannontech.common.constants.LoginController.YUKON_USER;
	private static final String SAVED_YUKON_USER = com.cannontech.common.constants.LoginController.SAVED_YUKON_USER;
		
/**
 * Handles login authentication, logout.
 * TODO: add change of password
 * Creation date: (5/5/00 10:48:46 AM)
 * @param req javax.servlet.http.HttpServletRequest
 * @param resp javax.servlet.http.HttpServletResponse
 * @exception javax.servlet.ServletException The exception description.
 * @exception java.io.IOException The exception description.
 */
public void service(HttpServletRequest req, HttpServletResponse resp) throws javax.servlet.ServletException, java.io.IOException 
{
	String action = req.getParameter(ACTION).toString();
	String nextURI = req.getParameter(REDIRECT);
		
	if(LOGIN.equalsIgnoreCase(action)) {
		String username = req.getParameter(USERNAME);
		String password = req.getParameter(PASSWORD);
		LiteYukonUser user = AuthFuncs.login(username,password);
		String home_url = null;
		
		if(user != null && 
			(home_url = AuthFuncs.getRolePropertyValue(user,WebClientRole.HOME_URL)) != null) {
			HttpSession session = req.getSession(true);
			try {						
				initSession(user, session);
				
				// Remember where they came from								
				nextURI = AuthFuncs.getRolePropertyValue(user, WebClientRole.LOG_OFF_URL, "");
				if(nextURI.length() > 0 && !nextURI.equalsIgnoreCase(CtiUtilities.STRING_NONE)) {
					Cookie c = new Cookie(Integer.toString(WebClientRole.LOG_OFF_URL), nextURI);
					c.setPath("/");
					resp.addCookie(c);
				}				
			} catch(TransactionException e) {
				session.invalidate();
				resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			}

			resp.sendRedirect(home_url);
		}
		else {
			if (nextURI == null)
				nextURI = req.getContextPath() + INVALID_URI;
			resp.sendRedirect(nextURI);
		}		
	}
	else 
	if(LOGOUT.equalsIgnoreCase(action)) {
		HttpSession session = req.getSession();
		if(session != null) {
			LiteYukonUser user = (LiteYukonUser) session.getAttribute(YUKON_USER);
			if (user != null && nextURI == null) {
				nextURI = AuthFuncs.getRolePropertyValue(user, WebClientRole.LOG_OFF_URL, "");
				if (nextURI.length() == 0 || nextURI.equalsIgnoreCase(CtiUtilities.STRING_NONE))
					nextURI = LOGIN_URI;
			}
			
			LiteYukonUser savedUser = (LiteYukonUser) session.getAttribute(SAVED_YUKON_USER);
			session.invalidate();
			if (savedUser != null && !savedUser.equals(user)) {
				// Restore saved yukon user into session after logging off
				session = req.getSession( true );
				session.setAttribute(SAVED_YUKON_USER, savedUser);
			}
		}

		if (nextURI == null) nextURI = LOGIN_URI;
		if (nextURI.charAt(0) == '/')
			nextURI = req.getContextPath() + nextURI;
		resp.sendRedirect(nextURI);
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


private void initSession(LiteYukonUser user, HttpSession session) throws TransactionException  {
	session.setAttribute(com.cannontech.common.constants.LoginController.YUKON_USER, user);
}
}
