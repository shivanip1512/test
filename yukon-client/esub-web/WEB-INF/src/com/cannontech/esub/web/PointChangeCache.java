package com.cannontech.esub.web;

import java.util.logging.Logger;

/**
 * PointChangeCache provides the current value of all Yukon points.
 * 
 * A connection with dispatch is established using properties from
 * /config.properties
 * 
 * Relies on com.cannontech.database.PoolManager to obtain a 
 * connection to the database. (to get state info)
 * 
 * This class is thread hot.
 * 
 * Creation date: (7/17/02 12:52:09 PM)
 * @author: Aaron Lauinger
 * @see com.cannontech.message.dispatch.ClientConnection
 */

public class PointChangeCache  implements Runnable, java.util.Observer {	
	// Connection to dispatch
	private com.cannontech.message.dispatch.ClientConnection conn = null;
	
	// Stores current PointData messages by PointID
	// ( key = Integer, value = com.cannontech.message.dispatch.message.PointData)
	private java.util.Hashtable pointData = new java.util.Hashtable();
	
	// Stores current Signal messages by PointID
	// ( key = Integer, value = com.cannontech.message.dispatch.message.Signal)
	private java.util.Hashtable signalData = new java.util.Hashtable();
		
	// Thread to harvest the incoming messages
	private Thread runner = null;

	//Date of the last point change received from dispatch
	private java.util.Date lastChange = null;
	
	// Logger to use
	private Logger logger;
	
/**
 * SingletonVangoghConnection constructor comment.
 */
protected PointChangeCache()
{
	super();
	
	logger = Logger.getLogger(getClass().toString());
}
/**
 * Insert the method's description here.
 * Creation date: (9/7/00 11:16:29 AM)
 */
public synchronized void connect() 
{
	String vgHost = "localhost";
	int vgPort = 1510;

	//figure out where vangogh is
		java.io.InputStream is = getClass().getResourceAsStream("/config.properties");
		java.util.Properties props = new java.util.Properties();
		try
		{
			props.load(is);
		}
		catch (Exception e)
		{
			logger.severe("Can't read the properties file. " + "Make sure /config.properties is in the CLASSPATH");
			return;
		}
		vgHost = props.getProperty("vangogh_machine");
		String portStr = props.getProperty("vangogh_port");
		try
		{
			vgPort = Integer.parseInt(portStr);
		}
		catch (NumberFormatException ne)
		{
			logger.warning("unable to determine dispatch");
			return;
		}
		
		logger.fine("attempting to connect to vangogh @" + vgHost + ":" + vgPort);
		conn = new com.cannontech.message.dispatch.ClientConnection();
		conn.addObserver(this);
		conn.setHost(vgHost);
		conn.setPort(vgPort);
		com.cannontech.message.dispatch.message.Registration reg = new com.cannontech.message.dispatch.message.Registration();
		reg.setAppName("PointChangeCache " + (new java.util.Date()).getTime());
		reg.setAppIsUnique(0);
		reg.setAppKnownPort(0);
		reg.setAppExpirationDelay(5000);
		com.cannontech.message.dispatch.message.PointRegistration pReg = new com.cannontech.message.dispatch.message.PointRegistration();
		pReg.setRegFlags(com.cannontech.message.dispatch.message.PointRegistration.REG_ALL_PTS_MASK |
							com.cannontech.message.dispatch.message.PointRegistration.REG_ALARMS );
		com.cannontech.message.dispatch.message.Multi multi = new com.cannontech.message.dispatch.message.Multi();
		multi.getVector().addElement(reg);
		multi.getVector().addElement(pReg);
		conn.setRegistrationMsg(multi);
		conn.setAutoReconnect(true);
		conn.setTimeToReconnect(30);
	
	try
	{
		conn.connectWithoutWait();
	}
	catch( java.io.IOException io )
	{
		io.printStackTrace();
		logger.warning("An error occured connecting with dispatch");
	}

	if( runner == null )
	{
		runner = new Thread(this);
		runner.start();
	}
}
/**
 * Insert the method's description here.
 * Creation date: (9/7/00 11:19:05 AM)
 */
public synchronized void disconnect() 
{
	try
	{
		conn.disconnect();
		conn.deleteObservers();
	}
	catch( java.io.IOException io )
	{
		io.printStackTrace();
		logger.warning("An error occured connecting with dispatch");
	}

	//interrupt the runner thread and join with it
	if( runner != null )
	{
		runner.interrupt();

		try
		{
			runner.join();			
		}
		catch( InterruptedException ie )
		{
			ie.printStackTrace();
		}
		
		runner = null;
	}	
}
/**
 * Insert the method's description here.
 * Creation date: (9/13/00 1:44:18 PM)
 * @return java.util.Date
 */
public java.util.Date getLastChange() {
	return lastChange;
}
/**
 * Insert the method's description here.
 * Creation date: (3/31/00 1:03:43 PM)
 * @return com.cannontech.servlet.PointData
 * @param pointId long
 */
public com.cannontech.message.dispatch.message.Signal getSignal(long pointId) {
	return (com.cannontech.message.dispatch.message.Signal) signalData.get( new Long(pointId) );
}
/**
 * Insert the method's description here.
 * Creation date: (3/31/00 1:03:43 PM)
 * @return com.cannontech.servlet.PointData
 * @param pointId long
 */
public String getState(long pointId, double value, String dbAlias) 	
{
	com.cannontech.message.dispatch.message.PointData pData = (com.cannontech.message.dispatch.message.PointData) pointData.get( new Long(pointId) );

	if( pData != null )
	{
		return retrieveState( (int) pData.getId(), pData.getValue(), dbAlias );
	}
	else
	{
		return "-";
	}
}
/**
 * Insert the method's description here.
 * Creation date: (3/31/00 1:03:43 PM)
 * @return com.cannontech.servlet.PointData
 * @param pointId long
 */
public com.cannontech.message.dispatch.message.PointData getValue(long pointId) {
	return (com.cannontech.message.dispatch.message.PointData) pointData.get( new Long(pointId) );
}
/**
 * Insert the method's description here.
 * Creation date: (12/27/2000 5:06:48 PM)
 * @param msg com.cannontech.message.util.Message
 */
private void handleMessage(com.cannontech.message.util.Message msg)
{
	if (msg instanceof com.cannontech.message.dispatch.message.Multi)
	{
		java.util.Vector inMessages = ((com.cannontech.message.dispatch.message.Multi) msg).getVector();
		java.util.Iterator iter = inMessages.iterator();
		while( iter.hasNext() )		
			handleMessage( (com.cannontech.message.util.Message) iter.next() );				
	}
	else
	if (msg instanceof com.cannontech.message.dispatch.message.PointData)
	{
		Long id = new Long(((com.cannontech.message.dispatch.message.PointData) msg).getId());		
		pointData.put(id, msg);		
		
		logger.fine("Received point data for point id:  " + id);
	}
	else
	if (msg instanceof com.cannontech.message.dispatch.message.Signal)
	{		
		com.cannontech.message.dispatch.message.Signal signal =
			(com.cannontech.message.dispatch.message.Signal) msg;

		Long id = new Long( signal.getId() );

		logger.fine("Received signal id:  " + id + " tags:  " + signal.getTags() );
		
			
		if( (signal.getTags() & com.cannontech.message.dispatch.message.Signal.MASK_ANY_ALARM) != 0 )
		{						
			signalData.put( id, signal );
			logger.fine("Storing signal");
		}
		else
		{
			signalData.remove( id );
			logger.fine("Removing signal");
		
		}		
	}
	else
	{
		logger.warning("received an unknown message of class:  " + msg.getClass() );
	}
	
	lastChange = msg.getTimeStamp();
}

/**
 * Insert the method's description here.
 * Creation date: (3/30/00 4:33:36 PM)
 * @param pData com.cannontech.message.dispatch.message.PointData
 */
private void insertValue(com.cannontech.message.dispatch.message.PointData pData) 
{
	Long id = new Long(pData.getId());

	pointData.put(id, pData );
}

/**
 * Creation date: (1/23/2002 4:41:41 PM)
 * @param args java.lang.String[]
 */
public static void main(String[] args) {
	
	PointChangeCache pcc = new PointChangeCache();
	Thread t = new Thread(pcc);
	t.start();
	
}
/**
 * Insert the method's description here.
 * Creation date: (1/3/2001 12:47:01 PM)
 * @return java.lang.String
 * @param pointid int
 * @param value double
 */
private synchronized String retrieveState(int pointid, double value, String dbAlias) 
{
	String sql = "SELECT State.Text FROM Point,StateGroup,State WHERE State.StateGroupID=StateGroup.StateGroupID AND StateGroup.StateGroupID=Point.StateGroupID AND State.RawState=" + value + " AND " + "Point.PointID=" + pointid;
	String state = "-";
	
	java.sql.Connection conn = null;
	java.sql.Statement stmt = null;
	java.sql.ResultSet rset = null;

	try
	{
		conn = com.cannontech.database.PoolManager.getInstance().getConnection(dbAlias);
		stmt = conn.createStatement();
		rset = stmt.executeQuery(sql);

		if( rset.next() )
		{
			state = rset.getString(1);			
		}
	}
	catch( java.sql.SQLException e )
	{
		e.printStackTrace();
	}
	finally
	{
		try
		{
			if( stmt != null ) stmt.close();
			if( conn != null ) conn.close();
		} catch( Exception e ) { }
	}

	return state;
}
/**
 * Insert the method's description here.
 * Creation date: (3/30/00 4:27:52 PM)
 */
public void run()
{		
	try
	{
		logger.fine("starting up...");
		
		while(true)
		{
			Object in;
			while( (in = conn.read(0)) != null )
			{
				if( in instanceof com.cannontech.message.util.Message )
				{
					handleMessage( (com.cannontech.message.util.Message) in );
				}
				else
				{
					logger.warning("received an unknown message of class:  " + in.getClass());
				}
			}
			
			Thread.sleep(1000);

			if( !conn.isValid() )						
				logger.warning("connect to dispatch is down...");			
		}
	}
	catch( InterruptedException ie )
	{
		logger.fine("closing connection to dispatch");
	}
	finally
	{
		try
		{
			conn.disconnect();
		}
		catch( java.io.IOException ioe )
		{
			logger.warning("Error disconnecting with vangogh occured");
		}		
	}
}
/**
 * Updates from the client connect show up here
 * Creation date: (3/30/00 5:15:08 PM)
 * @param obs java.util.Observable
 * @param val java.lang.Object
 */
public void update(java.util.Observable obs, Object val) 
{
	if( obs instanceof com.cannontech.message.dispatch.ClientConnection )
	{
		if( conn.isValid() )
			logger.info("Connection established to " + conn.getHost() + ":" + conn.getPort());
		else
			logger.info("Connection to " + conn.getHost() + ":" + conn.getPort() + " is down");
	}
}
}
