package com.cannontech.servlet;

/**
 * Maintains a connection to a MACS Server.
 * Stores itself in its servlet context so other
 * servlets can access the connection.
 * Creation date: (3/21/2001 11:31:54 AM)
 * @author: Aaron Lauinger
 */

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiProperties;
import com.cannontech.macs.MACSClientConnection;

public class MACSConnectionServlet extends javax.servlet.http.HttpServlet implements java.util.Observer {

	// Key used to store instances of this in the servlet context
	public static final String SERVLET_CONTEXT_ID = "MACSConnection";
	private MACSClientConnection conn;
	
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
		CTILogger.error("An exception occured disconnecting from MACS", ioe);
	}

	super.destroy();
}
/**
 * Creation date: (3/21/2001 1:41:38 PM)
 * @return com.cannontech.macs.MACSClientConnection
 */
public MACSClientConnection getConnection() {
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

	CtiProperties props = CtiProperties.getInstance();

	String mcHost = props.getProperty("macs_machine","127.0.0.1");
	int mcPort = Integer.parseInt(props.getProperty("macs_port","1900"));

	CTILogger.info("Will attempt to connect to macs @" + mcHost + ":" + mcPort);
	conn = new com.cannontech.macs.MACSClientConnection();
	conn.addObserver(this);
	
	if( mcHost != null )
		conn.setHost(mcHost);

	if( mcPort != -1 )
		conn.setPort(mcPort);

	// Set a retrieve all schedule command as the registration command
	// so on every connect we receive all the schedules
	com.cannontech.message.macs.message.RetrieveSchedule msg = 
		new com.cannontech.message.macs.message.RetrieveSchedule();
	
	msg.setScheduleId(com.cannontech.message.macs.message.RetrieveSchedule.ALL_SCHEDULES);

	conn.setRegistrationMsg(msg);
	conn.setAutoReconnect(true);
	conn.setTimeToReconnect(30);

	try
	{
		conn.connectWithoutWait();
	}
	catch( java.io.IOException io )
	{
		io.printStackTrace();
		CTILogger.error("An error occured connecting with macs", io);
	}

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
