/*
 * Created on Sep 2, 2003
  */
package com.cannontech.message.dispatch.message;

import java.util.Date;

import com.cannontech.message.util.Message;

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
	
	private int instanceID;
	private int pointID;
	private int tagID;
	private String descriptionStr = "";
	private int action;
	private Date tagTime;
	private String referenceStr = "";
	private String taggedForStr = "";
	
	
	/**
	 * @return one of ADD_TAG_ACTION, REMOVE_TAG_ACTION, or UPDATE_TAG_ACTION
	 */
	public int getAction() {
		return action;
	}

	/**
	 * @return Tag description
	 */
	public String getDescriptionStr() {
		return descriptionStr;
	}

	/**
	 * Every tag as a unique one of these.
	 * @return instance id
	 */
	public int getInstanceID() {
		return instanceID;
	}

	/**
	 * @return id of the point this tag is for
	 */
	public int getPointID() {
		return pointID;
	}

	/**
	 * @return 
	 */
	public String getReferenceStr() {
		return referenceStr;
	}

	/**
	 * @return
	 */
	public String getTaggedForStr() {
		return taggedForStr;
	}

	/**
	 * 
	 * @return
	 */
	public int getTagID() {
		return tagID;
	}

	/**
	 * @return
	 */
	public Date getTagTime() {
		return tagTime;
	}

	/**
	 * One of ADD_TAG_ACTION, REMOVE_TAG_ACTION, or UPDATE_TAG_ACTION
	 * @param i
	 */
	public void setAction(int action) {
		if(action == ADD_TAG_ACTION 	||
		   action == REMOVE_TAG_ACTION 	||
		   action == UPDATE_TAG_ACTION ) {
		   	throw new IllegalArgumentException("Action must be one of ADD_TAG_ACTION, REMOVE_TAG_ACTION, or UPDATE_TAG_ACTION");
		   }
		   
		this.action = action;
	}

	/**
	 * Tag description
	 * @param string
	 */
	public void setDescriptionStr(String description) {
		this.descriptionStr = description;
	}

	/**
	 * Every tag as a unique one of these.
	 * Only dispatch (and restoreGuts) should set this.
	 * @param i
	 */
	void setInstanceID(int instanceID) {
		this.instanceID = instanceID;
	}

	/**
	 * The id of the point this tag is for
	 * @param i
	 */
	public void setPointID(int pointID) {
		this.pointID = pointID;
	}

	/**
	 * Job number, etc.
	 * We don't do anything with this field but pass it around and display it.
	 * @param string
	 */
	public void setReferenceStr(String refID) {
		this.referenceStr = refID;
	}

	/**
	 * Whom this tag is for?
	 * We don't do anything with this field but pass it around and display it.
	 * @param string
	 */
	public void setTaggedForStr(String taggedFor) {
		this.taggedForStr = taggedFor;
	}

	/**
	 * Must refer to a tag from the tag table.
	 * @param i
	 */
	public void setTagID(int tagID) {
		this.tagID = tagID;
	}

	/**
	 * When did this tag happen.
	 * Only dispatch (and restoreGuts) should set this. 
	 * @param date
	 */
	void setTagTime(Date tagTime) {
		this.tagTime = tagTime;
	}

}
