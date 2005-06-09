package com.cannontech.servlet;

/**
 * Creates and stores a connection to various servers
 * 
 */

import com.cannontech.yukon.IMACSConnection;
import com.cannontech.yukon.INotifConnection;
import com.cannontech.yukon.conns.ConnPool;

public class ConnServlet extends javax.servlet.http.HttpServlet
{
	// Key used to store instances of this in the servlet context
	public static final String SERVLETS_CONTEXT_ID = "AllConns";



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
 * Creation date: (8/8/00 1:54:34 PM)
 * @return IMACSConnection
 */
public INotifConnection getNotifcationConn() 
{
	return (INotifConnection)ConnPool.getInstance().getDefNotificationConn();
}
	
/**
 * Insert the method's description here.
 * Creation date: (3/21/2001 11:36:13 AM)
 */
public void destroy() 
{
	// Pull this out of the servlet context
	getServletContext().removeAttribute(SERVLETS_CONTEXT_ID);


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

	getServletContext().setAttribute(SERVLETS_CONTEXT_ID, this);

}

}
