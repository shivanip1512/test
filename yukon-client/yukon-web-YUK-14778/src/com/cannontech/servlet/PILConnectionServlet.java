package com.cannontech.servlet;

/**
 * Maintains a connection to Port Control Server.
 * Stores itself in its servlet context so other
 * servlets can access the connection.
 * 
 * Creation date: (3/21/2001 11:31:54 AM)
 * @author: Aaron Lauinger
 */

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import com.cannontech.message.util.MessageEvent;
import com.cannontech.message.util.MessageListener;
import com.cannontech.yukon.IServerConnection;
import com.cannontech.yukon.conns.ConnPool;

public class PILConnectionServlet extends ErrorAwareInitializingServlet implements MessageListener
{

	// Key used to store instances of this in the servlet context
	public static String SERVLET_CONTEXT_ID = "PILConnection";
	//private ClientConnection conn;

/**
 * Creation date: (3/21/2001 11:36:13 AM)
 */
public void destroy() 
{
	// Pull this out of the servlet context
	getServletContext().removeAttribute(SERVLET_CONTEXT_ID);

	super.destroy();
}

/**
 * Creation date: (3/21/2001 1:41:38 PM)
 * @return com.cannontech.macs.MACSClientConnection
 */
public IServerConnection getConnection()
{
    return ConnPool.getInstance().getDefPorterConn();        
}

/**
 * Makes a connection to MACS and stores a reference to this in
 * the servlet context.
 * Creation date: (3/21/2001 11:35:57 AM)
 * @param config javax.servlet.ServletConfig
 * @exception javax.servlet.ServletException The exception description.
 */
public void doInit(ServletConfig config) throws ServletException
{
    getConnection().addMessageListener( this );

	// Add this to the servlet context
	getServletContext().setAttribute(SERVLET_CONTEXT_ID, this);
}

public void messageReceived( MessageEvent msg )
{
}

}
