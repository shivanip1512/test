package com.cannontech.servlet;

/**
 * Maintains a connection to a LC Server.
 * Stores itself in its servlet context so other
 * servlets can access the connection.
 * Creation date: (3/21/2001 11:31:54 AM)
 * @author: Aaron Lauinger
 */

import com.cannontech.common.util.LogWriter;
import com.cannontech.loadcontrol.LoadControlClientConnection;
import com.cannontech.util.Constants;

public class LCConnectionServlet extends javax.servlet.http.HttpServlet implements java.util.Observer {
		
	// Key used to store instances of this in the servlet context
	public static final String SERVLET_CONTEXT_ID = "LCConnection";

	private static final String LOGFILE = "lcconn.log";
	
	private LogWriter logger;
	private LoadControlClientConnection conn;
	private com.cannontech.web.loadcontrol.LoadcontrolCache cache;
/**
 */
public LCConnectionServlet() {
	super();	
}
/**
 * Insert the method's description here.
 * Creation date: (3/21/2001 11:36:13 AM)
 */
public void destroy() 
{
	// Pull this out of the servlet context
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
		logger.log("An exception occured disconnecting from load control", LogWriter.ERROR );
	}

	logger.getPrintWriter().close();
	logger = null;
		
	super.destroy();
}
/**
 * Creation date: (6/25/2001 1:04:28 PM)
 * @return com.cannontech.web.loadcontrol.LoadcontrolCache
 */
public com.cannontech.web.loadcontrol.LoadcontrolCache getCache() {
	return cache;
}
/**
 * Creation date: (3/21/2001 1:41:38 PM)
 * @return com.cannontech.macs.LClientConnection
 */
public LoadControlClientConnection getConnection() {
	System.out.println("returning connection!");
	if( conn != null )
		System.out.println("and its not null!");
	return conn;
}
/**
 * Makes a connection to Loadcontrol and stores a reference to this in
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
					
		logger = new com.cannontech.common.util.LogWriter("LCConnectionServlet", com.cannontech.common.util.LogWriter.DEBUG, writer);
		logger.log("Starting up....", LogWriter.INFO);
	}
	catch (java.io.FileNotFoundException e)
	{
		e.printStackTrace();
	}

	String lcHost = "127.0.0.1";
	int lcPort = 1920;
	
	//figure out where LC is
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

	lcHost = props.getProperty("loadcontrol_machine");
	String portStr = props.getProperty("loadcontrol_port");

 	try
	{
		lcPort = Integer.parseInt(portStr);
	}
	catch (NumberFormatException ne)
	{
		logger.log("Unable to determine loadcontrol_port", com.cannontech.common.util.LogWriter.ERROR);
		return;
	}

	logger.log("Will attempt to connect to loadcontrol @" + lcHost + ":" + lcPort, com.cannontech.common.util.LogWriter.INFO);
	conn = LoadControlClientConnection.getInstance();
	conn.addObserver(this);
	
	if( lcHost != null )
		conn.setHost(lcHost);

	if( lcPort != -1 )
		conn.setPort(lcPort);
		
	conn.setAutoReconnect(true);	
	conn.setTimeToReconnect(30);

	try
	{
		conn.connectWithoutWait();
	}
	catch( java.io.IOException io )
	{
		io.printStackTrace();
		logger.log("An error occured connecting with load control", com.cannontech.common.util.LogWriter.ERROR );
	}

	// Create a load control cache
	cache = new com.cannontech.web.loadcontrol.LoadcontrolCache();
	conn.addObserver(cache);

	// Add this to the context so other servlets can access the connection
	getServletContext().setAttribute(SERVLET_CONTEXT_ID, this);		
}
/**
 * This is registered with the MACS Connection to receive updates
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
