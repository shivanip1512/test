package com.cannontech.common.cache;

import java.util.Date;
import java.util.Hashtable;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiProperties;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.cache.functions.PointFuncs;
import com.cannontech.database.cache.functions.StateFuncs;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.message.util.ClientConnection;
import com.cannontech.message.util.MessageEvent;
import com.cannontech.message.util.MessageListener;

/**
 * PointChangeCache provides the current value of all Yukon points.
 * 
 * A connection with dispatch is established using properties from
 * /config.properties
 * 
 * Relies on com.cannontech.database.PoolManager to obtain a 
 * connection to the database. (to get state info)
 * 
 * A singleton instance is made available via the getPointChangeCache 
 * method.
 * 
 * This class is thread hot.
 * 
 * Creation date: (7/17/02 12:52:09 PM)
 * @author: Aaron Lauinger
 * @see com.cannontech.message.dispatch.ClientConnection
 */

public class PointChangeCache  implements java.util.Observer, MessageListener  {	
	
	// Connection to dispatch
	private com.cannontech.message.dispatch.ClientConnection conn = null;

	// Stores current PointData messages by PointID
	// ( key = Integer, value = com.cannontech.message.dispatch.message.PointData)
	private Hashtable pointData = new Hashtable();
	
	// Stores current Signal messages by PointID
	// ( key = Integer, value = com.cannontech.message.dispatch.message.Signal)
	private Hashtable signalData = new Hashtable();
	
	// Stores current tags by PointID
	// (key = Integer, value = Long)	
	private Hashtable tagData = new Hashtable();

	//Date of the last point change received from dispatch
	private Date lastChange = null;
		
	// Singleton instance
	private static PointChangeCache instance;
	
public static synchronized PointChangeCache getPointChangeCache() {
 	if( instance == null ) {
		instance = new PointChangeCache();
		instance.connect();
	}
	
	return instance;
}

/**
 * SingletonVangoghConnection constructor comment.
 */
protected PointChangeCache()
{
	super();	
}
/**
 * Insert the method's description here.
 * Creation date: (9/7/00 11:16:29 AM)
 */
public synchronized void connect() 
{
	String host = CtiProperties.getInstance().getProperty(CtiProperties.KEY_DISPATCH_MACHINE, "127.0.0.1");
	String portStr = CtiProperties.getInstance().getProperty(CtiProperties.KEY_DISPATCH_PORT, "1510");
	int port = 1510;
	
	try {
		port = Integer.parseInt(portStr);
	} catch(NumberFormatException nfe) {
		CTILogger.warn("Bad value for " + CtiProperties.KEY_DISPATCH_PORT);		
	}			

	
	CTILogger.debug("attempting to connect to dispatch @" + host + ":" + port);
	conn = new com.cannontech.message.dispatch.ClientConnection();
	conn.addObserver(this);
	conn.setHost(host);
	conn.setPort(port);

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
	conn.addMessageListener(this); 
	conn.setQueueMessages(false);
	
	try
	{
		conn.connectWithoutWait();
	}
	catch( java.io.IOException e )
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
		CTILogger.warn("An error occured connecting with dispatch");
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
		com.cannontech.clientutils.CTILogger.error( io.getMessage(), io );
		CTILogger.warn("An error occured connecting with dispatch");
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

public String getState(long pointId, double value) {
	return getState(pointId, value, CtiUtilities.getDatabaseAlias());
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
		LitePoint lp = PointFuncs.getLitePoint((int)pointId);
		LiteState ls = StateFuncs.getLiteState((int) lp.getStateGroupID(), (int) value);
		return ls.getStateText();
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
 * Returns the tags for a point if available
 * @param pointId
 * @return
 */
public long getTags(long pointId) {
	Long t = (Long) tagData.get(new Long(pointId));
	return t == null ? 0 : t.longValue();
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
		PointData pd = (PointData) msg;
		Long id = new Long(pd.getId());
		Long tags = new Long(pd.getTags());		
		pointData.put(id, pd);		
		tagData.put(id, tags);
		CTILogger.debug("Received point data for point id:  " + id);
	}
	else
	if (msg instanceof com.cannontech.message.dispatch.message.Signal)
	{		
		com.cannontech.message.dispatch.message.Signal signal =
			(com.cannontech.message.dispatch.message.Signal) msg;

		Long id = new Long( signal.getId() );
		Long tags = new Long(signal.getTags());
		
		CTILogger.debug("Received signal id:  " + id + " tags:  " + signal.getTags() );
			
		if( (signal.getTags() & com.cannontech.message.dispatch.message.Signal.MASK_ANY_ALARM) != 0 )
		{						
			signalData.put( id, signal );
			CTILogger.debug("Storing signal");
		}
		else
		{
			signalData.remove( id );
			CTILogger.debug("Removing signal");
		
		}		
		tagData.put(id, tags);
	}
	else
	if(msg instanceof DBChangeMsg) {
		//handle the Cache's DBChangeMessages
		DefaultDatabaseCache.getInstance().handleDBChangeMessage( 
				(com.cannontech.message.dispatch.message.DBChangeMsg)msg );
		CTILogger.debug("Received DBChangeMsg from: " + msg.getSource());
	}
	else
	{
		CTILogger.warn("received an unknown message of class:  " + msg.getClass() );
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
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
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

public void messageReceived(MessageEvent e) {
	handleMessage(e.getMessage());	
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
			CTILogger.debug("Connection established to " + conn.getHost() + ":" + conn.getPort());
		else
			CTILogger.debug("Connection to " + conn.getHost() + ":" + conn.getPort() + " is down");
	}
}
/**
 * Don't use this unless you know why it could be bad.
 * @return
 */
public com.cannontech.message.dispatch.ClientConnection getDispatchConnection() {
	return conn;
}
}
