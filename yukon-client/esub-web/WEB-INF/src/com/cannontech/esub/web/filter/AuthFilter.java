package com.cannontech.esub.web.filter;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cannontech.esub.web.Authenticator;
import com.cannontech.esub.web.SessionInfo;

/**
 * @author alauinger
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class AuthFilter implements Filter {
	
	private static final String LOGIN_FORM_URL = "login.jsp";
	private static final String LOGIN_ERROR_URL = "login.jsp?failed=true";
	// Keys to store things
	private static final String LOGGED_IN		 = "LOGGED_IN";
	private static final String SESSION_INFO   = "SESSIONINFO";
	private static final String ORIGINAL_URL   = "ORIGINALURL";

	private static final String FORM_LOGIN    = "LOGIN";	
	private static final String FORM_USERNAME = "USERNAME";
	private static final String FORM_PASSWORD = "PASSWORD";
	
	private FilterConfig config;
	/**
	 * @see javax.servlet.Filter#init(FilterConfig)
	 */
	public void init(FilterConfig config) throws ServletException {
		this.config = config;
	}

	/**
	 * @see javax.servlet.Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(
		ServletRequest req,
		ServletResponse resp,
		FilterChain chain)
		throws IOException, ServletException {


		HttpServletRequest hreq = (HttpServletRequest)req;
		HttpServletResponse hres = (HttpServletResponse)resp;

		String currentURI = hreq.getRequestURL().toString();
        String currentURL = hreq.getRequestURI();
	
		// get everything after the context root
        int firstSlash = currentURL.indexOf("/",1); // jump past the starting slash
        String targetURL = null;
        if (firstSlash != -1) targetURL = currentURL.substring(firstSlash + 1, currentURL.length());

		Logger.global.info("Auth Filter invoked, currentURI=" + currentURI + "  currentURL=" + currentURL);
				
		// Check for a the login field, if set do try to valid them 	
		String doLogin = hreq.getParameter(FORM_LOGIN);				
        if (doLogin != null) {
            validateLogin(req, resp, chain);
            // jump out of this method, shouldn't get here
            return;
        }

		// check if the user is signed on
        boolean signedOn = false;
        if (hreq.getSession().getAttribute(LOGGED_IN) != null) {
            signedOn =((Boolean)hreq.getSession().getAttribute(LOGGED_IN)).booleanValue();
        } else {
            hreq.getSession().setAttribute(LOGGED_IN, new Boolean(false));
        }
        
        // jump to the resource if signed on
        // FIXFIXFIX do a check on the target to see if they are allowed
        // to access this resource!!        
        if (signedOn) {
         	chain.doFilter(req,resp);
            return;
        }
        else {
        	// put the orginal url in the session so others can access
            hreq.getSession().setAttribute(ORIGINAL_URL,  targetURL);
            config.getServletContext().getRequestDispatcher("/" + LOGIN_FORM_URL).forward(req, resp);
            //hres.sendRedirect(LOGIN_FORM_URL);
            // Jump out of the filter and go to the next page
             return;        	
        }			
	}
	
	/**
	 * @see javax.servlet.Filter#destroy()
	 */
	public void destroy() {
		this.config = null;
	}

	private void validateLogin(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
		// convert to a http servlet request for now
        HttpServletRequest hreq = (HttpServletRequest)req;
        HttpServletResponse hres = (HttpServletResponse)resp;
        
        String username = hreq.getParameter(FORM_USERNAME);        
        String password = hreq.getParameter(FORM_PASSWORD);

		SessionInfo info = Authenticator.login(username, password);
		if( info != null ) {
			//login looks get, set up the session and redirect them 
			//where they wanted to go
			hreq.getSession().setAttribute(LOGGED_IN, Boolean.TRUE);
			hreq.getSession().setAttribute(SESSION_INFO,info);			
            String targetURL = (String)hreq.getSession().getAttribute(ORIGINAL_URL);
			Logger.global.info("redirecting authenticated user to: " + targetURL);
            hres.sendRedirect(targetURL);
            return;
		}
		else {
			Logger.global.info("failed login, redirecting user to: " + LOGIN_ERROR_URL);
			config.getServletContext().getRequestDispatcher("/" + LOGIN_ERROR_URL).forward(req, resp);
			//hres.sendRedirect(LOGIN_ERROR_URL);
            return;	
		}
	}

}
