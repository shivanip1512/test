package com.cannontech.servlet;

/**
 * Maintains a connection to a MACS Server.
 * Stores itself in its servlet context so other
 * servlets can access the connection.
 * Creation date: (3/21/2001 11:31:54 AM)
 * @author: Aaron Lauinger
 */

import com.cannontech.yukon.IMACSConnection;
import com.cannontech.yukon.conns.ConnPool;


public class MACSConnectionServlet extends javax.servlet.http.HttpServlet /*implements java.util.Observer*/ {

	// Key used to store instances of this in the servlet context
	public static final String SERVLET_CONTEXT_ID = "MACSConnection";
	//private MACSClientConnection conn;



/**
 * Insert the method's description here.
 * Creation date: (8/8/00 1:54:34 PM)
 * @return IMACSConnection
 */
public IMACSConnection getIMACSConnection() 
{
    return (IMACSConnection)ConnPool.getInstance().getDefMacsConn();
}

	
/**
 * Insert the method's description here.
 * Creation date: (3/21/2001 11:36:13 AM)
 */
public void destroy() 
{
	// Pull this out of the servlet context
	getServletContext().removeAttribute(SERVLET_CONTEXT_ID);


	super.destroy();
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
/*
	try
	{
		getIMACSConnection().sendRetrieveAllSchedules();
	}
	catch( IOException e )
	{
		CTILogger.error( "Could not RetrieveAllSchedules", e );
	}
*/
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
/*public void update(java.util.Observable obs, Object o) 
{
	if( obs == conn )
	{		
		if( getIMACSConnection().getMACSConnBase().isValid() )
			CTILogger.info(
				"Connection established to " + getIMACSConnection().getMACSConnBase().getHost() + ":" 
				+ getIMACSConnection().getMACSConnBase().getPort());
		else
			CTILogger.info("Connection to " + 
				getIMACSConnection().getMACSConnBase().getHost() + ":" + 
				getIMACSConnection().getMACSConnBase().getPort() + " is down");
	}
	else
	{
		CTILogger.info("Warning!  received an update from an unknown observable!");
	}
}
*/

}
