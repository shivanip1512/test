/*
 * Created on Jun 23, 2003
 */
package com.cannontech.common.login;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.LoginController;

/**
 * Handles the dirty work of contacting servlets.
 * @author aaron
 */
class LoginSupport {
	static String getSessionID(String yukonHost, int port, String username, String password) throws RuntimeException {
		
		URL url;
		try {
	
			url = new URL("http", yukonHost, port, "/servlet/LoginController?" + 
				LoginController.USERNAME + "=" + username + "&" + LoginController.PASSWORD + "=" +
				password + "&" + LoginController.ACTION + "=" + LoginController.CLIENT_LOGIN);
		}
		catch(MalformedURLException e) {
			throw new RuntimeException("Bad URL", e);			
		}
		
		HttpURLConnection conn = null;
		HttpURLConnection.setFollowRedirects(false);
		
		InputStream in = null;
		try {			
			conn = (HttpURLConnection) url.openConnection();
			
			in = conn.getInputStream();
			
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
	
	static Properties getDBProperties(String sessionID, String yukonHost, int port) {
		URL url;
		try {
			url = new URL("http", yukonHost, port, "/servlet/DBPropertiesServlet");
		}
		catch(MalformedURLException e) {
			throw new RuntimeException("Bad URL", e);			
		}
	 	HttpURLConnection conn = null;

	 	try {
	 		conn = (HttpURLConnection) url.openConnection();	 		
	 		conn.setRequestProperty("Cookie", sessionID);
	 		InputStream in = conn.getInputStream();			
	 		Properties dbProps = new Properties();
			dbProps.load(in);			
			return dbProps;
	 	}
	 	catch(IOException e) {
	 		try {
	 			throw new RuntimeException(conn.getResponseMessage());
	 		}
	 		catch(IOException ioe2) { throw new RuntimeException(e.getMessage()); }
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
			conn.getContent();
		} catch (IOException e1) {
			throw new RuntimeException(e1.getMessage());
		}
	}
}