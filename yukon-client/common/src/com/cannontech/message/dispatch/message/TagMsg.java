/*
 * Created on Sep 2, 2003
  */
package com.cannontech.message.dispatch.message;

import java.util.Date;

import com.cannontech.message.util.Message;
import com.cannontech.tags.Tag;

/**
 * Message that makes adding, removing, and updating tags for a point possible.
 * Not to be confused with tags in the pointdata/signal messages.
 * @author aaron
 */
public class TagMsg extends Message {

	// Possible values for the action field
	public static final int ADD_TAG_ACTION = 0;
	public static final int REMOVE_TAG_ACTION = 1;
	public static final int UPDATE_TAG_ACTION = 2;
	public static final int REPORT_TAG_ACTION = 3;
	
	private Tag _tag = new Tag();
	private int _action;
	private int _clientMessageID;
	
	/**
	 * @return one of ADD_TAG_ACTION, REMOVE_TAG_ACTION, or UPDATE_TAG_ACTION
	 */
	public int getAction() {
		return _action;
	}

	/**
	 * @return Tag description
	 */
	public String getDescriptionStr() {
		return _tag.getDescriptionStr();
	}

	/**
	 * Every tag as a unique one of these.
	 * @return instance id
	 */
	public int getInstanceID() {
		return _tag.getInstanceID();
	}

	/**
	 * @return id of the point this tag is for
	 */
	public int getPointID() {
		return _tag.getPointID();
	}

	/**
	 * @return 
	 */
	public String getReferenceStr() {
		return _tag.getReferenceStr();
	}

	/**
	 * @return
	 */
	public String getTaggedForStr() {
		return _tag.getTaggedForStr();
	}

	/**
	 * 
	 * @return
	 */
	public int getTagID() {
		return _tag.getTagID();
	}

	/**
	 * @return
	 */
	public Date getTagTime() {
		return _tag.getTagTime();
	}

	/**
	 * One of ADD_TAG_ACTION, REMOVE_TAG_ACTION, or UPDATE_TAG_ACTION
	 * @param i
	 */
	public void setAction(int action) {
		if( !(action == ADD_TAG_ACTION 		||
		      action == REMOVE_TAG_ACTION 	||
		      action == UPDATE_TAG_ACTION  ||
		      action == REPORT_TAG_ACTION )) {
		   	throw new IllegalArgumentException("Action must be one of ADD_TAG_ACTION, REMOVE_TAG_ACTION, UPDATE_TAG_ACTION, or REPORT_TAG_ACTION");
		   }
		   
		this._action = action;
	}

	/**
	 * Tag description
	 * @param string
	 */
	public void setDescriptionStr(String description) {
		_tag.setDescriptionStr(description);
	}

	/**
	 * Every tag as a unique one of these.
	 * Only dispatch (and restoreGuts) should set this.
	 * @param i
	 */
	void setInstanceID(int instanceID) {
		_tag.setInstanceID(instanceID);
	}

	/**
	 * The id of the point this tag is for
	 * @param i
	 */
	public void setPointID(int pointID) {
		_tag.setPointID(pointID);
	}

	/**
	 * Job number, etc.
	 * We don't do anything with this field but pass it around and display it.
	 * @param string
	 */
	public void setReferenceStr(String refID) {
		_tag.setReferenceStr(refID);
	}

	/**
	 * Whom this tag is for?
	 * We don't do anything with this field but pass it around and display it.
	 * @param string
	 */
	public void setTaggedForStr(String taggedFor) {
		_tag.setTaggedForStr(taggedFor);
	}

	/**
	 * Must refer to a tag from the tag table.
	 * @param i
	 */
	public void setTagID(int tagID) {
		_tag.setTagID(tagID);
	}

	/**
	 * When did this tag happen.
	 * Only dispatch (and restoreGuts) should set this. 
	 * @param date
	 */
	void setTagTime(Date tagTime) {
		_tag.setTagTime(tagTime);
	}

	/**
	 * @return
	 */
	public Tag getTag() {
		return _tag;
	}

	/**
	 * @param tag
	 */
	public void setTag(Tag tag) {
		_tag = tag;
	}
	/**
	 * @return
	 */
	public int getClientMessageID() {
		return _clientMessageID;
	}

	/**
	 * @param i
	 */
	public void setClientMessageID(int i) {
		_clientMessageID = i;
	}

	public void setUserName(String newUserName) {
		super.setUserName(newUserName);
		_tag.setUsername(newUserName);
	}

}
