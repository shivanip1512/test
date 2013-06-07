/*
 * Created on Dec 8, 2003
 */
package com.cannontech.tags;

import java.util.Date;

/**
 * Tag value object.  
 */
public class Tag {
	private int instanceID;
	private int pointID;
	private int tagID;
	private String username = "";
	private String descriptionStr = "";
	private Date tagTime = new Date();
	private String referenceStr = "";
	private String taggedForStr = "";
	
	/**
	 * @return
	 */
	public String getDescriptionStr() {
		return descriptionStr;
	}

	/**
	 * @return
	 */
	public int getInstanceId() {
		return instanceID;
	}

	/**
	 * @return
	 */
	public int getPointId() {
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
	 * @return
	 */
	public int getTagId() {
		return tagID;
	}

	/**
	 * @return
	 */
	public Date getTagTime() {
		return tagTime;
	}

	/**
	 * @param string
	 */
	public void setDescriptionStr(String string) {
		descriptionStr = string;
	}

	/**
	 * @param i
	 */
	public void setInstanceId(int i) {
		instanceID = i;
	}

	/**
	 * @param i
	 */
	public void setPointId(int i) {
		pointID = i;
	}

	/**
	 * @param string
	 */
	public void setReferenceStr(String string) {
		referenceStr = string;
	}

	/**
	 * @param string
	 */
	public void setTaggedForStr(String string) {
		taggedForStr = string;
	}

	/**
	 * @param i
	 */
	public void setTagId(int i) {
		tagID = i;
	}

	/**
	 * @param date
	 */
	public void setTagTime(Date date) {
		tagTime = date;
	}	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {		
		return getInstanceId();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		return (obj instanceof Tag &&
				((Tag)obj).getInstanceId() == getInstanceId());
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "Tag instanceID: " + getInstanceId() + " pointID: " + getPointId();
	}

	/**
	 * @return
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param string
	 */
	public void setUsername(String string) {
		username = string;
	}

}
