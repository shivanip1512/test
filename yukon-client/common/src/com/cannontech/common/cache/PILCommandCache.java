/*
 * Created on Nov 2, 2003
 */
package com.cannontech.common.cache;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Timer;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.Pair;
import com.cannontech.database.cache.functions.RoleFuncs;
import com.cannontech.message.dispatch.message.Multi;
import com.cannontech.message.porter.ClientConnection;
import com.cannontech.message.porter.message.Request;
import com.cannontech.message.porter.message.Return;
import com.cannontech.message.util.MessageEvent;
import com.cannontech.message.util.MessageListener;
import com.cannontech.roles.yukon.SystemRole;

/**
 * PILCommandCache provides stateless clients a way to have a converstation with PIL.
 * It provides:
 * A way to write Request messages to PIL.
 * A way match request and return messages.  
 * 
 * A client can send a message to PIL and as long as it can remember the id of the request message
 * it can query the cache for any response messages.
 * 
 * @author aaron
 */
public class PILCommandCache implements MessageListener, Observer {
	
	// Check the cache for things to purge this often (ms)
	private static final int PURGE_INTERVAL = 300;// * 1000;
	 
	//connection to PIL
	private ClientConnection pilConn = null;
	
	//message ID, this is how we match responses with requests
	private int messageID = 1;
	
	// Map to store requests and responees to those requests
	// Map< Integer, Pair< List< Request> , List< Response > >
	private Map reqRetMap = new HashMap();
	
	// Timer to clear the cache of old stuff
	Timer purgeTimer = new Timer(true);
	
	// How long to keep requests
	private long maxAge = 60L * 60L * 24L * 1000L;
	
	//singleton instance
	private static PILCommandCache instance = null;
		
	// A timer task to purge the hashmap of old entries
	/*private class PurgeTask extends TimerTask {		
		public void run() {
			long now = System.currentTimeMillis();
			synchronized(reqRetMap) {
				Iterator iter = reqRetMap.entrySet().iterator();
				while(iter.hasNext()) {
					Object key = iter.next();
					Pair p = (Pair) reqRetMap.get(key);					
					List reqList = (List) p.first;
					long newestTS = findNewest(reqList);
					if(now > newestTS + PILCommandCache.this.maxAge) {
						CTILogger.info("purging request message created: " + new java.util.Date(newestTS));
						iter.remove();
					}
	 			}
			}
		}
	*/
		
		/** 
		 * find the oldest request in the list
		 * @param reqList
		 * @return
		 */
	/*private long findNewest(List reqList) {
			long oldest = Long.MAX_VALUE;
			Iterator i = reqList.iterator();
			while(i.hasNext()) {
				Request r = (Request) i.next();
				if(r.getTimeStamp().getTime() < oldest) {
					oldest = r.getTimeStamp().getTime();
				}
			}
			return oldest;
		}
	}
	*/
	/**
	 * Writes a request message to PIL.  The message ID assigned to this request is returned.
	 * @param reqMsg
	 * @return messageID
	 */
	public int write(Request reqMsg) {
		Multi multi = new Multi();
		multi.getVector().add(reqMsg);
		return write(multi);
	}
	
	/**
	 * Writes a number of request messages to PIL.  The message ID assigned to each of these request is
	 * returned.
	 * @param messageID
	 * @return
	 */
	public int write(Multi reqMsgList) {
		int id = generateMessageID();

		synchronized(reqRetMap) {
			Iterator mIter = reqMsgList.getVector().iterator();
			while(mIter.hasNext()) {
				Request req = (Request) mIter.next();
				req.setUserMessageID(id);
			}

			Pair reqEntry = new Pair(reqMsgList.getVector().subList(0, reqMsgList.getVector().size()), new ArrayList());
			reqRetMap.put(new Integer(id), reqEntry);
		}		
		pilConn.write(reqMsgList);
		
		
		return id;
	}
	/**
	 * Returns a list of Return messages that match the given messageID
	 * Returns null if there are no responses
	 * @param messageID
	 * @return
	 */
	public List getReturnMessages(int messageID) {
		Pair entry = getEntry(messageID);
		return (entry == null ? null : (List) entry.second);
	}
	
	/**
	 * Returns a list of Request messages that match the given messageID
	 * Returns null if there are no requests
	 * @param messageID
	 * @return
	 */
	public List getRequestMessages(int messageID) {
		Pair entry = getEntry(messageID);
		return (entry == null ? null : (List) entry.first);
	}

	public void clearMessages(int messageID) {
		synchronized(reqRetMap) {
			reqRetMap.remove(new Integer(messageID));
		}
	}
	/**
	 * Connection will let us know when a message comes in.
	 */
	public void messageReceived(MessageEvent e) {
		if(e.getMessage() instanceof Return) {
			Return returnMsg = (Return) e.getMessage();
	 		synchronized(reqRetMap) {
				List respList = getReturnMessages((int) returnMsg.getUserMessageID());
				if(respList != null) {
					respList.add(returnMsg);
				}
				else {
					CTILogger.warn("received an unkown PIL return messageid: " + returnMsg.getUserMessageID());
				}
			}
		}
	}
	
	/**
	 * Observable callback, the connection will let us know its status when it changes
	 */	
	public void update(Observable o, Object arg) {
		if(o instanceof ClientConnection) {
			if( pilConn.isValid() )
				CTILogger.debug("Connection established to " + pilConn.getHost() + ":" + pilConn.getPort());
			else
				CTILogger.debug("Connection to " + pilConn.getHost() + ":" + pilConn.getPort() + " is down");
		}
	}

	/**
	 * Connect to PIL
	 *
	 */
	private void connect() 
	{
		String host = RoleFuncs.getGlobalPropertyValue( SystemRole.PORTER_MACHINE );
		int port = 1510;

		try 
		{
			port = Integer.parseInt(
				RoleFuncs.getGlobalPropertyValue( SystemRole.PORTER_PORT ) );
		}
		catch(NumberFormatException nfe) 
		{
			CTILogger.warn("Bad value for PORTER_PORT property");		
		}			

		CTILogger.debug("attempting to connect to porter @" + host + ":" + port);
		pilConn = new ClientConnection();
		pilConn.addObserver(this);
		pilConn.setHost(host);
		pilConn.setPort(port);
		
		try {
			pilConn.connectWithoutWait();
		} catch(IOException ioe) {
			com.cannontech.clientutils.CTILogger.error( ioe.getMessage(), ioe );
			CTILogger.warn("An error occured connecting with porter");
		}			
	}
	
	/**
	 * accessor func to look up a message entry
	 * @param messageID
	 * @return
	 */
	private Pair getEntry(int messageID) {
		synchronized(reqRetMap) {
			return (Pair) reqRetMap.get(new Integer(messageID));
		}
	}
	
	/**
	 * generate a unique mesageid, don't let it be negative
	 * @return
	 */
	private synchronized int generateMessageID() {
		if(++messageID == Integer.MAX_VALUE) {
			messageID = 1;
		}
		return messageID;
	}
	
	/**
	 * Returns the singleton instance of this class
	 * @return
	 */
	public static synchronized PILCommandCache getInstance() {
		if(instance == null) {
			instance = new PILCommandCache();
			instance.connect();
		}
		return instance;
	}
	
	/**
	 * For now only allow a single instance of this
	 *
	 */
	private PILCommandCache() {
		//class cast when this runs, something is broken
		//purgeTimer.scheduleAtFixedRate(new PurgeTask(), PURGE_INTERVAL, PURGE_INTERVAL);
	}

}
