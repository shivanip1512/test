/*
 * Created on Jun 23, 2003
 */
package com.cannontech.common.login;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.LoginController;

/**
 * Handles the dirty work of contacting servlets.
 * @author aaron
 */
class LoginSupport {
	
	static String getSessionID(String yukonHost, int port, String username,
			String password) throws RuntimeException {
		
		URL url;
		try {
			
			String encodedUsername = null;
			String encodedPassword = null;
			try {
				encodedUsername = URLEncoder.encode(username, "UTF-8");
				encodedPassword = URLEncoder.encode(password, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException("There was a problem encoding the username or password.", e);
			}
			
			url = new URL("http", yukonHost, port, "/servlet/LoginController?" + 
				LoginController.USERNAME + "=" + encodedUsername + "&" + LoginController.PASSWORD + "=" +
				encodedPassword + "&" + LoginController.ACTION + "=" + LoginController.CLIENT_LOGIN);
		
		} catch(MalformedURLException e) {
			throw new RuntimeException("Bad URL", e);			
		}
		
		HttpURLConnection conn = null;
		HttpURLConnection.setFollowRedirects(false);
		
		try {			
			conn = (HttpURLConnection) url.openConnection();
			
			conn.getInputStream();
			
			String cookie = conn.getHeaderField("Set-Cookie");
			CTILogger.debug("LoginController returned 'Set-Cookie' = " + cookie);
			return cookie;
		}
		catch(IOException e) {
			try {
				int c = conn.getResponseCode();
				if(c == HttpURLConnection.HTTP_FORBIDDEN) {
					throw new RuntimeException("Invalid Username or Password.  Usernames and Passwords are case sensitive, be sure to use correct upper and lower case.");
				}
				else {
					throw new RuntimeException(e.getMessage());
				}
				} catch(IOException ioe2) { throw new RuntimeException(e.getMessage()); }					
		}
	}
	
	static void closeSession(String sessionID, String yukonHost, int port) {
		URL url;
		try {
			url = new URL("http", yukonHost, port, "/servlet/LoginController?ACTION=LOGOUT");
		}
		catch(MalformedURLException e) {
			throw new RuntimeException("Bad URL", e);
		}
		
		try {
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestProperty("Cookie", sessionID);
			HttpURLConnection.setFollowRedirects(true);
			conn.connect();
		} catch (IOException e1) {
			throw new RuntimeException(e1.getMessage());
		}
	}
}