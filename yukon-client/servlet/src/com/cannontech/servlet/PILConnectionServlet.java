package com.cannontech.servlet;

/**
 * Maintains a connection to Port Control Server.
 * Stores itself in its servlet context so other
 * servlets can access the connection.
 * Creation date: (3/21/2001 11:31:54 AM)
 * @author: Aaron Lauinger
 */

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.login.ClientSession;
import com.cannontech.message.porter.ClientConnection;
import com.cannontech.roles.yukon.SystemRole;

public class PILConnectionServlet extends javax.servlet.http.HttpServlet implements java.util.Observer {

	// Key used to store instances of this in the servlet context
	public static String SERVLET_CONTEXT_ID = "PILConnection";
	private ClientConnection conn;

/**
 * Creation date: (3/21/2001 11:36:13 AM)
 */
public void destroy() 
{
	// PUll this out of the servlet context
	getServletContext().removeAttribute(SERVLET_CONTEXT_ID);
	
	try
	{
		if( conn != null )
		{
			conn.disconnect();
		}
	}
	catch( java.io.IOException ioe )
	{
		CTILogger.error("An exception occured disconnecting from PIL", ioe);
	}

	super.destroy();
}
/**
 * Creation date: (3/21/2001 1:41:38 PM)
 * @return com.cannontech.macs.MACSClientConnection
 */
public ClientConnection getConnection() {
	return conn;
}
/**
 * Makes a connection to MACS and stores a reference to this in
 * the servlet context.
 * Creation date: (3/21/2001 11:35:57 AM)
 * @param config javax.servlet.ServletConfig
 * @exception javax.servlet.ServletException The exception description.
 */
public void init(javax.servlet.ServletConfig config) throws javax.servlet.ServletException
{
	super.init(config);

	String host = ClientSession.getInstance().getRolePropertyValue(
							SystemRole.PORTER_MACHINE,"127.0.0.1");
	int port = Integer.parseInt(
						ClientSession.getInstance().getRolePropertyValue(
							SystemRole.PORTER_PORT,"1540"));
	
	CTILogger.info("Will attempt to connect to porter @" + host + ":" + port);
	conn = new ClientConnection();
	conn.addObserver(this);
	
	if( host != null )
		conn.setHost(host);

	if( port != -1 )
		conn.setPort(port);

	conn.setAutoReconnect(true);
	conn.setTimeToReconnect(30);

	try
	{
		conn.connectWithoutWait();
	}
	catch( java.io.IOException io )
	{
		io.printStackTrace();
		CTILogger.error("An error occured connecting with porter", io);
	}

	// Add this to the servlet context
	getServletContext().setAttribute(SERVLET_CONTEXT_ID, this);
}
/**
 * This is registered with the PIL Connection to receive updates
 * on the connection state.
 * Creation date: (3/21/2001 11:43:09 AM)
 * @param conn java.util.Observable
 * @param o java.lang.Object
 */
public void update(java.util.Observable obs, Object o) 
{
	if( obs == conn )
	{		
		if( conn.isValid() )
			CTILogger.info("Connection established to " + conn.getHost() + ":" + conn.getPort());
		else
			CTILogger.info("Connection to " + conn.getHost() + ":" + conn.getPort() + " is down");
	}
	else
	{
		CTILogger.info("Warning!  received an update from an unknown observable!");
	}
}
}
