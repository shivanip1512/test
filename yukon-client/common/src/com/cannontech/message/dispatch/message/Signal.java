package com.cannontech.message.dispatch.message;

/**
 * Insert the type's description here.
 * Creation date: (1/28/00 11:05:07 AM)
 * @author: 
 */
public class Signal extends com.cannontech.message.util.Message 
{
	private long id = 0;

	// Identifies the signal type
	private int logType;

	// Logging priority - replaces classification
	// It would appear (from yukon.h header)that 6 is the most important and
	// 11 being the least important
	private long alarmStateID = 1;

	// What is this textually
	private java.lang.String description = "";

	// the short form of what happened
	private String action = "";

	//Alarm states - Bit field frome pointdefs.h
	private long tags = 0;

	// Signals (alarms & events)
	public static final long ALARM_SIGNAL = 255; // 1-255 are alarms
	public static final long EVENT_SIGNAL = 1;
	public static final int MAX_DISPLAYABLE_ALARM_SIGNAL = 11;

	// TAGS to be read from Dispatch
	// taken from pointdefs.h
	public final static int TAG_DISABLE_POINT_BY_POINT 		= 0x00000001;
	public final static int TAG_DISABLE_ALARM_BY_POINT 		= 0x00000002;
	public final static int TAG_DISABLE_CONTROL_BY_POINT 	= 0x00000004;
	public final static int TAG_DISABLE_DEVICE_BY_DEVICE 	= 0x00000010;
	public final static int TAG_DISABLE_ALARM_BY_DEVICE 		= 0x00000020;
	public final static int TAG_DISABLE_CONTROL_BY_DEVICE 	= 0x00000040;
	public final static int TAG_MANUAL 						= 0x00010000;
	public final static int TAG_EXTERNALVALUE 				= 0x00020000;
	public final static int TAG_CONTROL_SELECTED 			= 0x00040000;
	public final static int TAG_CONTROL_PENDING 				= 0x00080000;
	public final static int TAG_POINT_FORCE_UPDATE 			= 0x00001000;
	public final static int TAG_POINT_MUST_ARCHIVE 			= 0x00002000;
	public final static int TAG_POINT_MAY_BE_EXEMPTED 		= 0x00004000;
	public final static int TAG_ATTRIB_CONTROL_AVAILABLE 	= 0x10000000;
	public final static int TAG_ATTRIB_PSUEDO 				= 0x20000000;
	
	// TAGS for alarmed points (alarm states)
	public final static int TAG_UNACKNOWLEDGED_ALARM 		= 0x80000000;
	public final static int TAG_ACKNOWLEDGED_ALARM 			= 0x40000000;

	// masks to see if any tags are present
	public final static int MASK_RESETTABLE_TAGS 			= 0x00030000; 
	// tags which are reset upon any setPoint operation
	public final static int MASK_ANY_ALARM 					= 0xC0000000; 
	// detects UNACK or ACK alarm states
	public final static int MASK_ANY_CONTROL 				= 0x000C0000; 
	// detects CONTROL_SELECTED or CONTROL_PENDING
	public final static int MASK_ANY_DISABLE 				= 0x00000077;
	public final static int MASK_ANY_SERVICE_DISABLE 		= 0x00000011;
	public final static int MASK_ANY_ALARM_DISABLE 			= 0x00000022;
	public final static int MASK_ANY_CONTROL_DISABLE 		= 0x00000044;

/**
 * Signal constructor comment.
 */
public Signal() {
	super();
} 
/**
 * Insert the method's description here.
 * Creation date: (1/28/00 11:06:49 AM)
 * @return java.lang.String
 */
public java.lang.String getAction() {
	return action;
}
/**
 * Insert the method's description here.
 * Creation date: (5/22/00 10:29:03 AM)
 * @return long
 */
public long getAlarmStateID() {
	return alarmStateID;
}
/**
 * Insert the method's description here.
 * Creation date: (1/28/00 11:06:49 AM)
 * @return java.lang.String
 */
public java.lang.String getDescription() {
	return description;
}
/**
 * Insert the method's description here.
 * Creation date: (1/28/00 11:05:49 AM)
 * @return int
 */
public long getId() {
	return id;
}
/**
 * Insert the method's description here.
 * Creation date: (4/13/00 2:25:21 PM)
 * @return int
 */
public int getLogType() {
	return logType;
}
/**
 * Insert the method's description here.
 * Creation date: (1/28/00 11:07:48 AM)
 * @return long
 */
public int getTags() {
	return (int) tags;
}
/**
 * Insert the method's description here.
 * Creation date: (4/13/00 2:24:50 PM)
 * @param soeTag int
 */
public void setAction(String action) 
{
	this.action = action;
}
/**
 * Insert the method's description here.
 * Creation date: (5/22/00 10:29:03 AM)
 * @param newLogPriority long
 */
public void setAlarmStateID(long newAlarmStateID) {
	alarmStateID = newAlarmStateID;
}
/**
 * Insert the method's description here.
 * Creation date: (1/28/00 11:06:49 AM)
 * @param newStr java.lang.String
 */
public void setDescription(java.lang.String newDescription) {
	description = newDescription;
}
/**
 * Insert the method's description here.
 * Creation date: (1/28/00 11:05:49 AM)
 * @param newId int
 */
public void setId(int newId) {
	id = newId;
}
/**
 * Insert the method's description here.
 * Creation date: (1/28/00 11:05:49 AM)
 * @param newId int
 */
public void setId(long newId) {
	id = newId;
}
/**
 * Insert the method's description here.
 * Creation date: (4/13/00 2:25:39 PM)
 * @param logType int
 */
public void setLogType(int logType)
{
	this.logType = logType;
}
/**
 * Insert the method's description here.
 * Creation date: (1/28/00 11:07:48 AM)
 * @param newFlag long
 */
public void setTags(long newTags) {
	tags = newTags;
}
/**
 * Insert the method's description here.
 * Creation date: (1/31/00 4:42:32 PM)
 * @return java.lang.String
 */
public String toString() 
{
	String retStr = "com.cannontech.message.dispatch.message.Signal:\n";
	
	retStr += "Id:  " + getId() + "\n";
	retStr += "Log Type:  " + getLogType() + "\n";	
	retStr += "Logging Priority:  " + getAlarmStateID() + "\n";
	retStr += "Description:  " + getDescription() + "\n";
	retStr += "Action:  " + getAction() + "\n";
	retStr += "Tags:  " + getTags() + "\n";
	retStr += "UserName:  " + getUserName() + "\n";

	return retStr;
}
}
