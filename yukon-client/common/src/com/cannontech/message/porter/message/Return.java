package com.cannontech.message.porter.message;

import java.util.Vector;

/**
 * Insert the type's description here.
 * Creation date: (5/17/00 1:12:51 PM)
 * @author: 
 */
public class Return extends com.cannontech.message.util.Message
{
	private int deviceID;
	private java.lang.String commandString;
	private java.lang.String resultString;
	private int status;
	private int routeOffset;
	private int macroOffset;
	private int attemptNum;
	private int expectMore;
	private long transmissionID;
	private long userMessageID;
	
	
	private Vector vector = null;
	
/**
 * Return constructor comment.
 */
public Return() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (5/17/00 1:26:03 PM)
 * @return int
 */
public int getAttemptNum() {
	return attemptNum;
}
/**
 * Insert the method's description here.
 * Creation date: (5/17/00 1:25:05 PM)
 * @return java.lang.String
 */
public java.lang.String getCommandString() {
	return commandString;
}
/**
 * Insert the method's description here.
 * Creation date: (5/17/00 1:24:55 PM)
 * @return long
 */
public int getDeviceID() {
	return deviceID;
}
/**
 * Insert the method's description here.
 * Creation date: (5/17/00 1:26:12 PM)
 * @return int
 */
public int getExpectMore() {
	return expectMore;
}
/**
 * Insert the method's description here.
 * Creation date: (5/17/00 1:25:53 PM)
 * @return int
 */
public int getMacroOffset() {
	return macroOffset;
}
/**
 * Insert the method's description here.
 * Creation date: (5/17/00 1:25:16 PM)
 * @return java.lang.String
 */
public java.lang.String getResultString() {
	return resultString;
}
/**
 * Insert the method's description here.
 * Creation date: (5/17/00 1:25:42 PM)
 * @return int
 */
public int getRouteOffset() {
	return routeOffset;
}
/**
 * Insert the method's description here.
 * Creation date: (5/17/00 1:25:25 PM)
 * @return int
 */
public int getStatus() {
	return status;
}
/**
 * Insert the method's description here.
 * Creation date: (5/17/00 1:26:24 PM)
 * @return long
 */
public long getTransmissionID() {
	return transmissionID;
}
/**
 * Insert the method's description here.
 * Creation date: (5/17/00 1:26:36 PM)
 * @return long
 */
public long getUserMessageID() {
	return userMessageID;
}
/**
 * Insert the method's description here.
 * Creation date: (5/17/00 1:26:03 PM)
 * @param newAttemptNum int
 */
public void setAttemptNum(int newAttemptNum) {
	attemptNum = newAttemptNum;
}
/**
 * Insert the method's description here.
 * Creation date: (5/17/00 1:25:05 PM)
 * @param newCommandString java.lang.String
 */
public void setCommandString(java.lang.String newCommandString) {
	commandString = newCommandString;
}
/**
 * Insert the method's description here.
 * Creation date: (5/17/00 1:24:55 PM)
 * @param newDeviceID long
 */
public void setDeviceID(int newDeviceID) {
	deviceID = newDeviceID;
}
/**
 * Insert the method's description here.
 * Creation date: (5/17/00 1:26:12 PM)
 * @param newExpectMore int
 */
public void setExpectMore(int newExpectMore) {
	expectMore = newExpectMore;
}
/**
 * Insert the method's description here.
 * Creation date: (5/17/00 1:25:53 PM)
 * @param newMacroOffset int
 */
public void setMacroOffset(int newMacroOffset) {
	macroOffset = newMacroOffset;
}
/**
 * Insert the method's description here.
 * Creation date: (5/17/00 1:25:16 PM)
 * @param newResultString java.lang.String
 */
public void setResultString(java.lang.String newResultString) {
	resultString = newResultString;
}
/**
 * Insert the method's description here.
 * Creation date: (5/17/00 1:25:42 PM)
 * @param newRouteOffset int
 */
public void setRouteOffset(int newRouteOffset) {
	routeOffset = newRouteOffset;
}
/**
 * Insert the method's description here.
 * Creation date: (5/17/00 1:25:25 PM)
 * @param newStatus int
 */
public void setStatus(int newStatus) {
	status = newStatus;
}
/**
 * Insert the method's description here.
 * Creation date: (5/17/00 1:26:24 PM)
 * @param newTransmissionID long
 */
public void setTransmissionID(long newTransmissionID) {
	transmissionID = newTransmissionID;
}
/**
 * Insert the method's description here.
 * Creation date: (5/17/00 1:26:36 PM)
 * @param newUserMessageID long
 */
public void setUserMessageID(long newUserMessageID) {
	userMessageID = newUserMessageID;
}

/**
 * Insert the method's description here.
 * Creation date: (1/28/00 11:50:21 AM)
 * @return java.util.Vector
 */
public java.util.Vector getVector() {
	if( vector == null )
	{
		vector = new Vector();
	}
	
	return vector;
}
}
