package com.cannontech.message.dispatch.message;

/**
 * Insert the type's description here.
 * Creation date: (1/28/00 11:17:22 AM)
 * @author: 
 */
public class PointData extends com.cannontech.message.util.Message 
{
	private int id;
	private int type;
	private long quality;
	private long tags;
	private long attributes; // not used as of 11-20-2000
	private long limit;
	private double value;
	private java.lang.String str = "";
	private java.util.Date time = new java.util.Date();
	private long forced;


	// Point Types
	/* DEFINED IN com.cannontech.database.data.point.PointTypes */

	// point quality
	/* DEFINED IN com.cannontech.database.data.point.PointQualities */
/**
 * PointData constructor comment.
 */
public PointData() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (1/28/00 11:18:00 AM)
 * @return int
 */
public long getAttributes() {
	return attributes;
}
/**
 * Insert the method's description here.
 * Creation date: (2/1/00 12:12:27 PM)
 * @return long
 */
public long getForced() {
	return forced;
}
/**
 * Insert the method's description here.
 * Creation date: (1/28/00 11:17:38 AM)
 * @return int
 */
public int getId() {
	return id;
}
/**
 * Insert the method's description here.
 * Creation date: (1/28/00 11:18:39 AM)
 * @return long
 */
public long getLimit() {
	return limit;
}
/**
 * Time that this point read/gathered. This is NOT the time this message was created!
 * Creation date: (2/2/00 10:14:36 AM)
 * @return java.util.Date
 */
public java.util.Date getPointDataTimeStamp() {
	return time;
}
/**
 * Insert the method's description here.
 * Creation date: (1/28/00 11:18:12 AM)
 * @return long
 */
public long getQuality() {
	return quality;
}
/**
 * Insert the method's description here.
 * Creation date: (1/28/00 11:19:03 AM)
 * @return java.lang.String
 */
public java.lang.String getStr() {
	return str;
}
/**
 * Insert the method's description here.
 * Creation date: (1/28/00 11:18:27 AM)
 * @return long
 */
public long getTags() {
	return tags;
}
/**
 * Insert the method's description here.
 * Creation date: (1/28/00 11:17:51 AM)
 * @return int
 */
public int getType() {
	return type;
}
/**
 * Insert the method's description here.
 * Creation date: (1/28/00 11:18:49 AM)
 * @return double
 */
public double getValue() {
	return value;
}
/**
 * Insert the method's description here.
 * Creation date: (1/28/00 11:18:00 AM)
 * @param newOffset int
 */
public void setAttributes(int newAttributes) {
	attributes = newAttributes;
}
/**
 * Insert the method's description here.
 * Creation date: (1/28/00 11:18:00 AM)
 * @param newOffset int
 */
public void setAttributes(long newAttributes) {
	attributes = newAttributes;
}
/**
 * Insert the method's description here.
 * Creation date: (2/1/00 12:12:27 PM)
 * @param newForced long
 */
public void setForced(long newForced) {
	forced = newForced;
}
/**
 * Insert the method's description here.
 * Creation date: (1/28/00 11:17:38 AM)
 * @param newId int
 */
public void setId(int newId) {
	id = newId;
}
/**
 * Insert the method's description here.
 * Creation date: (1/28/00 11:18:39 AM)
 * @param newLimit long
 */
public void setLimit(long newLimit) {
	limit = newLimit;
}
/**
 * Insert the method's description here.
 * Creation date: (1/28/00 11:18:12 AM)
 * @param newQuality long
 */
public void setQuality(long newQuality) {
	quality = newQuality;
}
/**
 * Insert the method's description here.
 * Creation date: (1/28/00 11:19:03 AM)
 * @param newStr java.lang.String
 */
public void setStr(java.lang.String newStr) {
	str = newStr;
}
/**
 * Insert the method's description here.
 * Creation date: (1/28/00 11:18:27 AM)
 * @param newTags long
 */
public void setTags(long newTags) {
	tags = newTags;
}
/**
 * Insert the method's description here.
 * Creation date: (2/2/00 10:14:36 AM)
 * @param newTime java.util.Date
 */
public void setTime(java.util.Date newTime) {
	time = newTime;
}
/**
 * Insert the method's description here.
 * Creation date: (1/28/00 11:17:51 AM)
 * @param newType int
 */
public void setType(int newType) {
	type = newType;
}
/**
 * Insert the method's description here.
 * Creation date: (1/28/00 11:18:49 AM)
 * @param newValue double
 */
public void setValue(double newValue) {
	value = newValue;
}
/**
 * Insert the method's description here.
 * Creation date: (1/31/00 4:44:25 PM)
 * @return java.lang.String
 */
public String toString() {
	String retStr = "com.cannontech.message.dispatch.message.PointData:  \n";

	retStr += "Id:  " + getId() + "\n";
	retStr += "Limit:  " + getLimit() + "\n";
	retStr += "Attributes:  " + getAttributes() + "\n";
	retStr += "String:  " + getStr() + "\n";
	retStr += "Tags:  " + getTags() + "\n";
	retStr += "Type:  " + getType() + "\n";
	retStr += "Value:  " + getValue() + "\n";	

	return retStr;
}
}
