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
	private String descriptionStr = "";
	private Date tagTime;
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
	public int getInstanceID() {
		return instanceID;
	}

	/**
	 * @return
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
	 * @param string
	 */
	public void setDescriptionStr(String string) {
		descriptionStr = string;
	}

	/**
	 * @param i
	 */
	public void setInstanceID(int i) {
		instanceID = i;
	}

	/**
	 * @param i
	 */
	public void setPointID(int i) {
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
	public void setTagID(int i) {
		tagID = i;
	}

	/**
	 * @param date
	 */
	public void setTagTime(Date date) {
		tagTime = date;
	}	
}
