/*
 * Created on Dec 8, 2003
 */
package com.cannontech.tags;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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
	
	//Store all the tags from dispatch
	//HashMap< Integer(pointid), Set< Tag >>
	private HashMap _allTags;
	
	//Singleton instance
	private static TagManager _instance;
	
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
	public void createTag(int pointID, int tagID, String username, String desc, String refStr, String forStr) {
		TagMsg tm = new TagMsg();
		tm.setAction(TagMsg.ADD_TAG_ACTION);
		tm.setPointID(pointID);
		tm.setTagID(tagID);
		tm.setUserName(username);
		tm.setDescriptionStr(desc);
		tm.setReferenceStr(refStr);
		tm.setTaggedForStr(forStr);
		writeMsg(tm);

		//TODO: wait for response and return it??
	}
	
	/**
	 * Remove a given tag
	 * @param tag
	 */
	public void removeTag(Tag tag) {
		TagMsg tm = new TagMsg();
		tm.setAction(TagMsg.REMOVE_TAG_ACTION);
		tm.setTag(tag);
		writeMsg(tm);
	}
	
	/**
	 * Updated a given tag.
	 * @param tag
	 */
	public void updateTag(Tag tag) {
		TagMsg tm = new TagMsg();
		tm.setAction(TagMsg.UPDATE_TAG_ACTION);
		tm.setTag(tag);
		writeMsg(tm); 
	}
	
	/**
	 * Returns a Set< Tag > for the given point id.
	 * An empty Set will be returned if there are currently no tags for the given point id.
	 * @param pointID
	 * @return
	 */
	public Set getTags(int pointID) {
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
	
	private void writeMsg(TagMsg msg) {
//		TODO: BAD! THERE IS A BETTER WAY TO DO THIS
		ClientConnection conn = PointChangeCache.getPointChangeCache().getDispatchConnection();
		conn.write(msg);
	}
	
	/**
	 * Called by the dispatch connection.
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
				if(tMsg.getAction() == TagMsg.ADD_TAG_ACTION ||
					tMsg.getAction() == TagMsg.UPDATE_TAG_ACTION) {
					CTILogger.debug("Adding tag: " + t.toString());
					Set ts = getTags(tagKey.intValue());
					ts.add(t);
				}
				else 
				if(tMsg.getAction() == TagMsg.REMOVE_TAG_ACTION){
					CTILogger.debug("Removing tag: " + t.toString());
					Set ts = getTags(tagKey.intValue());
					ts.remove(t);
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
		PointChangeCache.getPointChangeCache().getDispatchConnection().addMessageListener(this);
	}
}
