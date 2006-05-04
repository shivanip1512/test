package com.cannontech.common.cache;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.cache.functions.PointFuncs;
import com.cannontech.database.cache.functions.RoleFuncs;
import com.cannontech.database.cache.functions.StateFuncs;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.Multi;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.message.dispatch.message.PointRegistration;
import com.cannontech.message.dispatch.message.Signal;
import com.cannontech.message.util.Message;
import com.cannontech.message.util.MessageEvent;
import com.cannontech.message.util.MessageListener;
import com.cannontech.roles.yukon.SystemRole;

/**
 * PointChangeCache provides the current dynamic info for all Yukon points.
 * 
 * A connection with dispatch is established using properties from
 * /config.properties
 * 
 * Relies on the databasecache for point state info
 *  
 * A singleton instance is made available via the getPointChangeCache 
 * method.
 * 
 * Creation date: (7/17/02 12:52:09 PM)
 * @author: Aaron Lauinger
 */

public class PointChangeCache  implements MessageListener  {	
	
	// Connection to dispatch
	private com.cannontech.message.dispatch.ClientConnection conn = null;

	// Stores current PointData messages by PointID
	// ( key = Integer, value = com.cannontech.message.dispatch.message.PointData)
	private Map<Long, PointData> pointDataMap = new HashMap<Long, PointData>();
	
	// Stores current Signal messages by PointID
	// Only signals with a category > 1 will be stored
	// ( key = Integer, value = List<Signal>
	private Map<Long, List<Signal>> pointSignalsMap = new HashMap<Long, List<Signal>>();

	// Stores current tags by PointID
	// (key = Integer, value = Integer)	
	private Map<Long, Integer> pointTagMap = new HashMap<Long, Integer>();
	
	// Stores the current Signal message by Alarm Category ID
	// ( key = Integer, value = List<Signal>)
	private Map<Long, List<Signal>> categorySignalsMap = new HashMap<Long, List<Signal>>();
	
	// Stores current tags by alarm category id
	// ( key = Integer, value = Integer)
	private Map<Long, Integer> categoryTagMap = new HashMap<Long, Integer>();

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
 * Connect to dispatch.
 * TODO: Use a connection pool to get a connection.
 */
public synchronized void connect() 
{
	String host = RoleFuncs.getGlobalPropertyValue( SystemRole.DISPATCH_MACHINE );
	String portStr = RoleFuncs.getGlobalPropertyValue( SystemRole.DISPATCH_PORT );
	int port = 1510;
	
	try {
		port = Integer.parseInt(portStr);
	} catch(NumberFormatException nfe) {
		CTILogger.warn("Bad value for DISPATCH-PORT");		
	}			
	
	CTILogger.debug("attempting to connect to dispatch @" + host + ":" + port);
	conn = new com.cannontech.message.dispatch.ClientConnection();
	conn.setHost(host);
	conn.setPort(port);

	com.cannontech.message.dispatch.message.Registration reg = new com.cannontech.message.dispatch.message.Registration();
	reg.setAppName( "PointChangeCache " + CtiUtilities.getAppRegistration() );
	reg.setAppIsUnique(0);
	reg.setAppKnownPort(0);
	reg.setAppExpirationDelay(5000);
	PointRegistration pReg = new PointRegistration();
	pReg.setRegFlags(PointRegistration.REG_ALL_PTS_MASK |
							PointRegistration.REG_ALARMS );
								
	com.cannontech.message.dispatch.message.Multi multi = new com.cannontech.message.dispatch.message.Multi();
	multi.getVector().addElement(reg);
	multi.getVector().addElement(pReg);
	conn.setRegistrationMsg(multi);
	conn.setAutoReconnect(true);
	conn.setTimeToReconnect(30);
	conn.addMessageListener(this); 
	conn.setQueueMessages(false);
	
	conn.connectWithoutWait();
}
/**
 * Disconnect from dispatch.
 * TODO:  Is this even necessary?
 */
public synchronized void disconnect() 
{
	try
	{
		conn.disconnect();
		conn.deleteObservers();
	}
	catch( IOException io )
	{
		CTILogger.error( "An error occured connecting with dispatch", io );
	}	
}
/**
 * Returns the timestamp of the last point data received from Dispatch.
 * @return java.util.Date
 */
public Date getLastChange() {
	return lastChange;
}

/**
 * Returns a List<Signal> for a given point.
 * @param pointId
 * @return
 */
public synchronized List getSignals(long pointId) {
	List sigs = pointSignalsMap.get(new Long(pointId));
	if(sigs == null) {
		sigs = Collections.emptyList();
	}
	return sigs;
}

/**
 * Returns a List<Signal> for a given alarm category
 * @param alarmCategoryID
 * @return
 */
public synchronized List getSignalsForCategory(long alarmCategoryID) {
	List sigs = categorySignalsMap.get(new Long(alarmCategoryID));
	if(sigs == null) {
		sigs = Collections.emptyList();
	}
	return sigs;	
}

/**
 * Return the text representation of this state that the given point and value
 * TODO: This doesn't belong here, maybe move it to a data access type object
 * @return com.cannontech.servlet.PointData
 * @param pointId long
 */
public synchronized String getState(long pointId, double value) 	
{
	PointData pData = pointDataMap.get( new Long(pointId) );

	if( pData != null )
	{
		LitePoint lp = PointFuncs.getLitePoint((int)pointId);
		LiteState ls = StateFuncs.getLiteState(lp.getStateGroupID(), (int)value);
		return ls.getStateText();
	}
	else
	{ 
		return "-";
	}
}

/**
 * Return the current state that a point is in if avaialble.
 * Prefer this over getState(..)
 * @param pointID
 * @return
 */
public synchronized LiteState getCurrentState(long pointID) {
	PointData pData = pointDataMap.get( new Long(pointID) );
	LitePoint lp = PointFuncs.getLitePoint((int)pointID);
	
	return (pData == null || lp == null ? null :
			StateFuncs.getLiteState( lp.getStateGroupID(), (int) pData.getValue()));
}
/**
 * Return the most recent PointData for a given point
 * @return com.cannontech.servlet.PointData
 * @param pointId long
 */
public synchronized PointData getValue(long pointId) {
	return pointDataMap.get( new Long(pointId) );
}

/**
 * Updates the cache and sends the PointData message to dispatch.
 * @param pointDataMsg
 */
public synchronized void putValue(PointData pointDataMsg) {
    handleMessage(pointDataMsg);
    getDispatchConnection().write(pointDataMsg);
}

/**
 * Returns the tags for a point if available
 * @param pointId
 * @return
 */
public synchronized int getTags(long pointId) {
	Integer t = pointTagMap.get(new Long(pointId));
	return t == null ? 0 : t.intValue();
}

/**
 * Handles incoming messages from Dispatch.
 * @param msg com.cannontech.message.util.Message
 */
private synchronized void handleMessage(Message msg) {
	if (msg instanceof Multi) {
		List<Message> inMessages = ((Multi) msg).getVector();
		Iterator<Message> iter = inMessages.iterator();
		while( iter.hasNext() ) {
            handleMessage( iter.next() );
        }				
	}
	else 
    if (msg instanceof PointData) {
		PointData pd = (PointData) msg;
		Long id = new Long(pd.getId());
		pointDataMap.put(id, pd);		
		pointTagMap.put(id, new Integer((int) pd.getTags())); //Check this, ok?
		categoryTagMap.put(id, new Integer((int) pd.getTags()));
		CTILogger.debug("Received point data for point id:  " + id);
	}
	else
	if (msg instanceof Signal) {		
		Signal signal = (Signal) msg;

		Long id = new Long( signal.getPointID() );
		Long categoryId = new Long(signal.getCategoryID());		
		Integer tags = new Integer(signal.getTags());
				
		CTILogger.debug("Received signal id:  " + signal.getPointID() + " tags:  " + signal.getTags() );
		
		List<Signal> pointSignals = pointSignalsMap.get(id);
		if(pointSignals == null) { 
			pointSignals = new ArrayList<Signal>(4);
			pointSignalsMap.put(id, pointSignals);
		}

		List<Signal> categorySignals = categorySignalsMap.get(categoryId);
		if(categorySignals == null) {
			categorySignals = new ArrayList<Signal>(10);
			categorySignalsMap.put(categoryId, categorySignals);
		}
		
		/*
		 * Only store the signal if the top two bits indicate alarm activity
		 */
		pointSignals.remove(signal);
		categorySignals.remove(signal);
		
		if((signal.getTags() & Signal.MASK_ANY_ALARM) != 0) {
			pointSignals.add(signal);
			categorySignals.add(signal);
		}
		
		pointTagMap.put(id, tags);		
	}   
	else
	if(msg instanceof DBChangeMsg) {
		//handle the Cache's DBChangeMessages
		DefaultDatabaseCache.getInstance().handleDBChangeMessage( 
				(DBChangeMsg)msg );
		CTILogger.debug("Received DBChangeMsg from: " + msg.getSource());
	}
	
	lastChange = msg.getTimeStamp();
}

/**
 * Dispatch connection messaged received callback.
 */
public void messageReceived(MessageEvent e) {
	handleMessage(e.getMessage());	
}


/**
 * Don't use this unless you know why it could be bad.
 * @return
 */
public com.cannontech.message.dispatch.ClientConnection getDispatchConnection() {
	return conn;
}
}
