package com.cannontech.esub.util;

import java.net.URL;
import java.net.URLConnection;

/**
 * Maintains session state info.
 * Creation date: (1/4/2002 2:09:03 PM)
 * @author: Aaron Lauinger
 */
public class ClientSession {
	private static ClientSession instance = null;

	private String host;
	private int port;
	
	private String cookie;
	private String homeURI;

	//buffers for doing requests
	private char[] buf = new char[4096];
	private StringBuffer stringBuf = new StringBuffer();
	
/**
 * Session constructor comment.
 */
private ClientSession() {
	super();
}
/**
 * Creates a URL to a given file based on the established
 * session.
 * Creation date: (1/28/2002 3:56:03 PM)
 * @return java.net.URL
 * @param file java.lang.String
 */
public URL createURL(String file) throws java.net.MalformedURLException {
	return new URL("http", getHost(), getPort(), homeURI + "/" + file);
}
/**
 * Creation date: (1/8/2002 12:30:25 PM)
 * @return java.lang.String
 * @param uri java.lang.String
 * @param param java.lang.String[]
 * @param val java.lang.String[]
 */
public synchronized String doPost(String uri) {

	return doPost(uri, new String[0], new String[0] );
}
/**
 * Creation date: (1/8/2002 12:30:25 PM)
 * @return java.lang.String
 * @param uri java.lang.String
 * @param param java.lang.String[]
 * @param val java.lang.String[]
 */
public synchronized String doPost(String uri, String[] param, String[] val) {

	if( !isValid() )
		return null;
		
	java.io.BufferedWriter writer = null;
	java.io.BufferedReader rdr = null;
	
	try {
		//connect to the same place we established the session
		URL u = new URL("http", getHost(), getPort(), uri);
		URLConnection conn = u.openConnection();

		conn.setDoOutput(true);
		conn.setRequestProperty("Cookie", getCookie());
		
    	writer = new java.io.BufferedWriter(new java.io.OutputStreamWriter(conn.getOutputStream()));

    	// write out parameters and values
    	if( param.length > 0 ) {
	    	writer.write(param[0] + "=" + val[0]);
    	}
    	
    	for( int i = 1; i < param.length; i++ ) {
	    	writer.write("&" + param[i] + "=" + val[i]);
    	}
	    
   	    writer.flush();
	 
        rdr = new java.io.BufferedReader(new java.io.InputStreamReader(conn.getInputStream()));

        int r;
        StringBuffer stringBuf = new StringBuffer();
		while( (r = rdr.read(buf)) != -1 ) {
			stringBuf.append(buf, 0, r);
		}
		
		return stringBuf.toString();	
	}
	catch(java.io.IOException e) {
		e.printStackTrace();
	}
	catch(NumberFormatException ne) {
		ne.printStackTrace();
	}
	finally {
		
		if( writer != null ) {
			try { writer.close(); } catch(java.io.IOException e) { }
		}
		if( rdr != null ) {
			try { rdr.close(); } catch(java.io.IOException e) { }
		}
	}

	return null;
}
/**
 * Creation date: (1/4/2002 3:01:36 PM)
 * @param loginURL java.net.URL
 */
public boolean establishSession(URL loginURL, String username, String password) {


	URLConnection conn  = null;
	try {				
		conn = loginURL.openConnection();	
		conn.setDoOutput(true);
				
    	java.io.BufferedWriter writer = 
    		new java.io.BufferedWriter(new java.io.OutputStreamWriter(conn.getOutputStream()));
	    		
		writer.write("USERNAME=" + username + "&");
		writer.write("PASSWORD=" + password + "&");		
		writer.write("ACTION=QLOGIN&SERVICE=ESUBSTATION&DATABASEALIAS=yukon");

   	    writer.flush();
        writer.close();

        java.io.BufferedReader rdr = new java.io.BufferedReader( new java.io.InputStreamReader(conn.getInputStream()));
        String clientDir = rdr.readLine();
        rdr.close();
        System.out.println("Received a client uri of: " + clientDir);

        if( clientDir != null ) {	        
	        // login looks good
	        homeURI = clientDir;
	 		String newCookie = conn.getHeaderField("set-cookie");

	 		if( newCookie != null ) {
		 		cookie = newCookie;
	 		}

	 		// <theory>
	 		// There is a case where the login appears successful, ie the server returns the users home directory,
	 		// but it sets no cookie.  This seems to happen when there has been an earlier login using the
	 		// same underlying socket connection and/or the previous session has expired on the server
	 		// in this case try to use the existing cookie with the home directory the server just gave us
	 		// </theory>
        }
        else {
	        //bad login! do not let this thru! (see the previous <theory/> as to why this could happen by accident)
	        cookie = null;
        }
	        
		host = loginURL.getHost();
		port = loginURL.getPort();
	
		System.out.println("Cookie: " + cookie);
	}
	catch(java.io.IOException e) {
		e.printStackTrace();
	}
	finally {
		return ( cookie != null );
	}
	
}
/**
 * Creation date: (1/7/2002 12:27:05 PM)
 * @return java.lang.String
 */
public java.lang.String getCookie() {
	return cookie;
}
/**
 * Creation date: (1/8/2002 12:31:33 PM)
 * @return java.lang.String
 */
public java.lang.String getHost() {
	return host;
}
/**
 * Creation date: (1/8/2002 12:24:59 PM)
 * @return com.cannontech.esub.viewer.ClientSession
 */
public static synchronized ClientSession getInstance() {
	if( instance == null )
		instance = new ClientSession();

	return instance;
}
/**
 * Creation date: (1/8/2002 12:31:33 PM)
 * @return int
 */
public int getPort() {
	return port;
}
/**
 * Creation date: (1/4/2002 3:03:15 PM)
 * @return boolean
 */
public boolean isValid() {
	return ( cookie != null );
}
/**
 * Log out from the server we are connected to.
 * Creation date: (1/28/2002 2:26:09 PM)
 */
public void logout() {
	try {
		java.net.URL logoutURL = new URL("http", getHost(), getPort(), "/servlet/LoginController");		
		logout(logoutURL);
	}
	catch(java.io.IOException e) {
		e.printStackTrace();
	}	
}
/**
 * Log out from the server we are connected to.
 * Creation date: (1/28/2002 2:26:09 PM)
 */
private void logout(URL logoutURL) {
	try {		
		URLConnection conn = logoutURL.openConnection();	
		conn.setDoOutput(true);
				
    	java.io.BufferedWriter writer = 
    		new java.io.BufferedWriter(new java.io.OutputStreamWriter(conn.getOutputStream()));
	    		
		writer.write("ACTION=LOGOUT");

   	    writer.flush();
        writer.close();
       
        ((java.net.HttpURLConnection) conn).disconnect();
	}
	catch(java.io.IOException e) {
		e.printStackTrace();
	}	
}
}
