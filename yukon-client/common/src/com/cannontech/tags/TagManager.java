/*
 * Created on Dec 8, 2003
 */
package com.cannontech.tags;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.cache.PointChangeCache;
import com.cannontech.message.dispatch.ClientConnection;
import com.cannontech.message.dispatch.message.Multi;
import com.cannontech.message.dispatch.message.TagMsg;
import com.cannontech.message.util.Message;
import com.cannontech.message.util.MessageEvent;
import com.cannontech.message.util.MessageListener;

/**
 * TagManager handles tag manipulation
 * @author Aaron Lauinger
 */
public class TagManager implements MessageListener {
	
	//Wait this many millis for a response from dispatch
	private long DISPATCH_RESPONSE_TIMEOUT = 60L*1000L;
	 
	//Store all the tags from dispatch
	//HashMap< Integer(pointid), Set< Tag >>
	private HashMap _allTags = new HashMap();
	
	// The clientmessageID used in TagMsg, used to identify responses
	private int _clientMessageID;
	
	{ // init the starting clientmessageID
		Random r = new Random(System.currentTimeMillis());
		_clientMessageID = r.nextInt();
	}
	
	// Provides a way to retrieve a synch object given a clientmessageid
	// so that we can wait for messages from dispatch
	//Map<Integer(clientMessageID), Object(synch)>
	private Map _waitForIDMap = Collections.synchronizedMap(new HashMap());
	
	//Singleton instance
	private static TagManager _instance;
	
	//an optional reference to a Dispatch connection
	private ClientConnection _dispatchConn = null;
	
	
	public static synchronized TagManager getInstance() {
		if(_instance == null) {
			_instance = new TagManager();
		}
		return _instance;
	}

	/**
	 * Create a tag.  
	 * @param pointID
	 * @param tagID
	 * @param username
	 * @param desc
	 * @param refStr
	 * @param forStr
	 * @return
	 */	
	public void createTag(int pointID, int tagID, String username, String desc, String refStr, String forStr) throws Exception {
		TagMsg tm = new TagMsg();
		tm.setAction(TagMsg.ADD_TAG_ACTION);
		tm.setPointID(pointID);
		tm.setTagID(tagID);
		tm.setUserName(username);
		tm.setDescriptionStr(desc);
		tm.setReferenceStr(refStr);
		tm.setTaggedForStr(forStr);
		writeMsg(tm);
	}
	
	/**
	 * Remove a given tag
	 * @param tag
	 */
	public void removeTag(Tag tag, String username) throws Exception {
		tag.setTagTime(new Date());
		TagMsg tm = new TagMsg();
		tm.setAction(TagMsg.REMOVE_TAG_ACTION);
		tm.setTag(tag);
		tm.setUserName(username);
		writeMsg(tm);
	}
	
	/**
	 * Updated a given tag.
	 * @param tag
	 */
	public void updateTag(Tag tag, String username) throws Exception {
		tag.setTagTime(new Date());
		TagMsg tm = new TagMsg();
		tm.setAction(TagMsg.UPDATE_TAG_ACTION);
		tm.setTag(tag);
		tm.setUserName(username);

		writeMsg(tm); 
	}
	
	/**
	 * Returns a Set< Tag > for the given point id.
	 * An empty Set will be returned if there are currently no tags for the given point id.
	 * @param pointID
	 * @return
	 */
	public Set getTags(int pointID) {
		HashSet ts = new HashSet(getTagSet(pointID));
		return ts;
	}
	
	/**
	 * Returns the tag for the given pointid with the given instanceid.
	 * The pointid parameter is included only for efficiency.
	 * A version of this that only takes an instanceID is possible but
	 * more expensive.
	 * @param pointID
	 * @param instanceID
	 * @return
	 */
	public Tag getTag(int pointID, int instanceID) {
		Set tagSet = getTags(pointID);
		Iterator tagIter = tagSet.iterator();
		while(tagIter.hasNext()) {
			Tag tag = (Tag) tagIter.next();
			if(tag.getInstanceID() == instanceID) {
				return tag;
			}
		}
		return null;
	}
	
	/**
	 * Returns a Set with all the points that current have tags.
	 * @return
	 */
	public Set getPointIDs() {
		HashSet ptSet = new HashSet();
		
		Iterator i =_allTags.keySet().iterator();
		while(i.hasNext()) {
			Integer pID = (Integer) i.next();
			Set tSet = getTagSet(pID.intValue());
			if(tSet.size() > 0) {
				ptSet.add(pID);
			}
		}
		return ptSet;
	}
	
	/**
	 * Returns the internal tag set for a given point id.
	 * Do not give this out.
	 * @param pointID
	 * @return
	 */
	private Set getTagSet(int pointID) {

		Integer tagKey = new Integer(pointID);
		synchronized(_allTags) {
			Set ts =  (Set) _allTags.get(tagKey);
			if(ts == null) {
				ts = new HashSet();
				_allTags.put(tagKey, ts);	
			}

			return ts;
		}
	}
	
	/**
	 * Write the given tag message.  Sets the clientMessageID in the message in order to wait for a response
	 * from dispatch before returning.
	 * @param msg
	 */
	private void writeMsg(TagMsg msg) throws Exception 
	{
		Integer msgID = new Integer(nextClientMessageID());
		msg.setClientMessageID(msgID.intValue());

		Object synchObj = new Object();

		try {		
			_waitForIDMap.put(msgID, synchObj);	
			synchronized(synchObj) {
				getDispatchConn().write(msg);
				synchObj.wait(DISPATCH_RESPONSE_TIMEOUT);
			}
		}
		finally {
			if(_waitForIDMap.remove(msgID) != null) {
				throw new Exception("Timed out waiting for a TagMsg response from Dispatch");		
			}
		}
	}
	
	/**
	 * Called by the dispatch connection.  Dont' call this directly
	 */
	public void messageReceived(MessageEvent me) {
		Message msg = me.getMessage();
		handleMessage(msg);
	}
	
	private void handleMessage(Message msg) {
		if(msg instanceof Multi) {
			Iterator mIter = ((Multi)msg).getVector().iterator();
			while(mIter.hasNext()) {
				handleMessage((Message)mIter.next());
			}
		}
		else
		if(msg instanceof TagMsg) {
			TagMsg tMsg = (TagMsg) msg;
			Tag t = (Tag) tMsg.getTag();
			Integer tagKey = new Integer(t.getPointID());
			
			synchronized(_allTags) {
				if(tMsg.getAction() == TagMsg.ADD_TAG_ACTION 	 ||
					tMsg.getAction() == TagMsg.UPDATE_TAG_ACTION ||
					tMsg.getAction() == TagMsg.REPORT_TAG_ACTION ) {
					CTILogger.debug("Adding/Updating/Reporting tag: " + t.getInstanceID());
					Set ts = getTagSet(tagKey.intValue());
					ts.add(t);
				}
				else 
				if(tMsg.getAction() == TagMsg.REMOVE_TAG_ACTION){
					CTILogger.debug("Removing tag: " + t.getInstanceID());
					Set ts = getTagSet(tagKey.intValue());
					ts.remove(t);
				}
			} 
			//Notify anyone waiting for this message if any
			Integer msgID = new Integer(tMsg.getClientMessageID());
			Object synchObj = _waitForIDMap.get(msgID);
			if(synchObj != null) {
				synchronized(synchObj) {
					_waitForIDMap.remove(msgID);
					synchObj.notifyAll();
				}
			}
		}
	}
	
	/**
	 * Don't create me directly please!
	 *
	 */
	private TagManager() {
		//TODO: BAD!
		getDispatchConn().addMessageListener(this);
		getDispatchConn().write(getDispatchConn().getRegistrationMsg());
	}
	
	/**
	 * gets the current dispatch connection we should use
	 * 
	 */
	private synchronized ClientConnection getDispatchConn()
	{
		if( _dispatchConn == null )
			return PointChangeCache.getPointChangeCache().getDispatchConnection();
		else
			return _dispatchConn;
	}

	/**
	 * Allow for others to give me a connection to Dispatch I can use
	 * Only use this constructor if needed!!
	 */
	public TagManager( ClientConnection conn )
	{
		if( conn == null )
			throw new IllegalArgumentException("Need a non null connection for the constructor");

		_dispatchConn = conn;
		_dispatchConn.addMessageListener(this);
	}
	
	/**
	 * Generate the next client message id
	 * @return
	 */
	private synchronized int nextClientMessageID() {
		return _clientMessageID++;
	}

}
