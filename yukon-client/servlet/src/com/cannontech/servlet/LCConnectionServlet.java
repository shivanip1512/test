package com.cannontech.servlet;

/**
 * Maintains a connection to a LC Server.
 * Stores itself in its servlet context so other
 * servlets can access the connection.
 * Creation date: (3/21/2001 11:31:54 AM)
 * @author: Aaron Lauinger
 */
 
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.login.ClientSession;
import com.cannontech.database.cache.DBChangeListener;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.loadcontrol.LoadControlClientConnection;
import com.cannontech.message.dispatch.ClientConnection;
import com.cannontech.roles.yukon.SystemRole;

public class LCConnectionServlet extends javax.servlet.http.HttpServlet implements java.util.Observer {
		
	// Key used to store instances of this in the servlet context
	public static final String SERVLET_CONTEXT_ID = "LCConnection";

	private LoadControlClientConnection conn;
	private com.cannontech.web.loadcontrol.LoadcontrolCache cache;
	

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
		CTILogger.error("An exception occured disconnecting from load control");
	}
	
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


	String lcHost = "127.0.0.1";
	int lcPort = 1920;

	try {
		lcHost =
			ClientSession.getInstance().getRolePropertyValue(
				SystemRole.LOADCONTROL_MACHINE,
				"127.0.0.1");
		lcPort =
			Integer.parseInt(
		ClientSession.getInstance().getRolePropertyValue(
					SystemRole.LOADCONTROL_PORT,
					"1920"));

	} catch (Exception e) {
		com.cannontech.clientutils.CTILogger.error(e.getMessage(), e);
	}

	CTILogger.info("Will attempt to connect to loadcontrol @" + lcHost + ":" + lcPort);
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
		CTILogger.error("An error occured connecting with load control", io);
	}

	// Create a load control cache
	cache = new com.cannontech.web.loadcontrol.LoadcontrolCache();
	conn.addObserver(cache);

	// Add this to the context so other servlets can access the connection
	getServletContext().setAttribute(SERVLET_CONTEXT_ID, this);		
	
	DBChangeListener dbl = new DBChangeListener() {
		ClientConnection dispatchConnection = null;
		public com.cannontech.message.util.ClientConnection getClientConnection() 
		{
			if (dispatchConnection == null) {

			String host;
			int port;

			try {
				host =
					ClientSession.getInstance().getRolePropertyValue(
						SystemRole.DISPATCH_MACHINE,
						"127.0.0.1");
				port =
					Integer.parseInt(
				ClientSession.getInstance().getRolePropertyValue(
							SystemRole.DISPATCH_PORT,
							"1510"));

			} catch (Exception e) {
				com.cannontech.clientutils.CTILogger.error(e.getMessage(), e);
				return null;
			}

			dispatchConnection = new com.cannontech.message.dispatch.ClientConnection();
			com.cannontech.message.dispatch.message.Registration reg =
				new com.cannontech.message.dispatch.message.Registration();
			reg.setAppName(
				"LCConnectionServlet @" + com.cannontech.common.util.CtiUtilities.getUserName());
			reg.setAppIsUnique(0);
			reg.setAppKnownPort(0);
			reg.setAppExpirationDelay(300); // 5 minutes should be OK

			//conn.addObserver(this);
			dispatchConnection.setHost(host);
			dispatchConnection.setPort(port);
			dispatchConnection.setAutoReconnect(true);
			dispatchConnection.setRegistrationMsg(reg);

			try {
				dispatchConnection.connectWithoutWait();
			} catch (Exception e) {
				com.cannontech.clientutils.CTILogger.error(e.getMessage(), e);
			}
		}
		
			return dispatchConnection;
		}
		public void handleDBChangeMsg(com.cannontech.message.dispatch.message.DBChangeMsg msg, com.cannontech.database.data.lite.LiteBase lBase ) { }	
	};
	
	DefaultDatabaseCache.getInstance().addDBChangeListener(dbl);
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
/*	if( obs == conn ) was spamming so commented out
	{		
		if( conn.isValid() )
			CTILogger.info("Connection established to " + conn.getHost() + ":" + conn.getPort());
		else
			CTILogger.info("Connection to " + conn.getHost() + ":" + conn.getPort() + " is down");
	}
	else
	{
		CTILogger.info("Warning!  received an update from an unknown observable!");
	}*/
}
}
