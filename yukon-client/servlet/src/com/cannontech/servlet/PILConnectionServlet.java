package com.cannontech.servlet;

/**
 * Maintains a connection to Port Control Server.
 * Stores itself in its servlet context so other
 * servlets can access the connection.
 * Creation date: (3/21/2001 11:31:54 AM)
 * @author: Aaron Lauinger
 */

import com.cannontech.message.porter.ClientConnection;
import com.cannontech.common.util.LogWriter;

import com.cannontech.util.Constants;

public class PILConnectionServlet extends javax.servlet.http.HttpServlet implements java.util.Observer {

	// Key used to store instances of this in the servlet context
	public static String SERVLET_CONTEXT_ID = "PILConnection";

	private static final String LOGFILE = "pilconn.log";
	
	private LogWriter logger;
	private ClientConnection conn;
/**
 * MACSConnectionServlet constructor comment.
 */
public PILConnectionServlet() {
	super();
}
/**
 * Insert the method's description here.
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
		logger.log("An exception occured disconnecting from PIL", LogWriter.ERROR );
	}

	logger.getPrintWriter().close();
	logger = null;
	
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

	try
	{		
		java.io.PrintWriter writer = 
			new java.io.PrintWriter( new java.io.FileOutputStream(LOGFILE, true ));
			
		logger = new com.cannontech.common.util.LogWriter("PILConnectionServlet", com.cannontech.common.util.LogWriter.DEBUG, writer);
		logger.log("Starting up....", LogWriter.INFO);	
	}
	catch (java.io.FileNotFoundException e)
	{
		e.printStackTrace();
	}
	
	String host = "127.0.0.1";
	int port = 1540;

	//figure out where PIL is
	java.io.InputStream is = getClass().getResourceAsStream(Constants.CONFIG_PROPERTIES);
	java.util.Properties props = new java.util.Properties();

	try
	{
		props.load(is);
	}
	catch (Exception e)
	{
		logger.log("Can't read the properties file. " + "Make sure db.properties is in the CLASSPATH", com.cannontech.common.util.LogWriter.ERROR);
		return;
	}
	
	host = props.getProperty("porter_machine");
	String portStr = props.getProperty("porter_port");
	
	try
	{
		port = Integer.parseInt(portStr);
	}
	catch (NumberFormatException ne)
	{
		logger.log("Unable to determine porter_port", com.cannontech.common.util.LogWriter.ERROR);
		return;
	}
		
	logger.log("Will attempt to connect to porter @" + host + ":" + port, com.cannontech.common.util.LogWriter.INFO);
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
		logger.log("An error occured connecting with porter", com.cannontech.common.util.LogWriter.ERROR );
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
			logger.log("Connection established to " + conn.getHost() + ":" + conn.getPort(), LogWriter.INFO );
		else
			logger.log("Connection to " + conn.getHost() + ":" + conn.getPort() + " is down", LogWriter.INFO);
	}
	else
	{
		logger.log("Warning!  received an update from an unknown observable!", LogWriter.ERROR );
	}
}
}
