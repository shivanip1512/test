package com.cannontech.message.porter.message;

import org.springframework.core.style.ToStringCreator;

/**
 * Insert the type's description here.
 * Creation date: (5/17/00 1:12:27 PM)
 * @author: 
 */
public class Request extends com.cannontech.message.util.Message 
{
	private int deviceID = 0;
	private java.lang.String commandString = "";
	private int routeID = 0;	
	private int macroOffset = 0;
	private int attemptNum = 0;
	private long transmissionID = 0;
	private long userMessageID = 0;
	private int optionsField = 0;
/**
 * Request constructor comment.
 */
public Request() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (5/17/00 2:29:03 PM)
 * @param deviceID long
 * @param commandString java.lang.String
 * @param userMessageID long
 */
public Request(int deviceID, String commandString, long userMessageID) 
{
	setDeviceID(deviceID);
	setCommandString(commandString);
	setUserMessageID(userMessageID);
}
/**
 * Insert the method's description here.
 * Creation date: (5/17/00 1:20:13 PM)
 * @return int
 */
public int getAttemptNum() {
	return attemptNum;
}
/**
 * Insert the method's description here.
 * Creation date: (5/17/00 1:19:18 PM)
 * @return java.lang.String
 */
public java.lang.String getCommandString() {
	return commandString;
}
/**
 * Insert the method's description here.
 * Creation date: (5/17/00 1:18:24 PM)
 * @return long
 */
public int getDeviceID() {
	return deviceID;
}
/**
 * Insert the method's description here.
 * Creation date: (5/17/00 1:20:02 PM)
 * @return int
 */
public int getMacroOffset() {
	return macroOffset;
}
/**
 * Insert the method's description here.
 * Creation date: (5/17/00 1:23:56 PM)
 * @return int
 */
public int getOptionsField() {
	return optionsField;
}
/**
 * Insert the method's description here.
 * Creation date: (5/17/00 1:19:38 PM)
 * @return long
 */
public int getRouteID() {
	return routeID;
}
/**
 * Insert the method's description here.
 * Creation date: (5/17/00 1:23:33 PM)
 * @return long
 */
public long getTransmissionID() {
	return transmissionID;
}
/**
 * Insert the method's description here.
 * Creation date: (5/17/00 1:23:46 PM)
 * @return long
 */
public long getUserMessageID() {
	return userMessageID;
}
/**
 * Insert the method's description here.
 * Creation date: (5/17/00 1:20:13 PM)
 * @param newAttemptNum int
 */
public void setAttemptNum(int newAttemptNum) {
	attemptNum = newAttemptNum;
}
/**
 * Insert the method's description here.
 * Creation date: (5/17/00 1:19:18 PM)
 * @param newCommandString java.lang.String
 */
public void setCommandString(java.lang.String newCommandString) {
	commandString = newCommandString;
}
/**
 * Insert the method's description here.
 * Creation date: (5/17/00 1:18:24 PM)
 * @param newDeviceID long
 */
public void setDeviceID(int newDeviceID) {
	deviceID = newDeviceID;
}
/**
 * Insert the method's description here.
 * Creation date: (5/17/00 1:20:02 PM)
 * @param newMacroOffset int
 */
public void setMacroOffset(int newMacroOffset) {
	macroOffset = newMacroOffset;
}
/**
 * Insert the method's description here.
 * Creation date: (5/17/00 1:23:56 PM)
 * @param newOptionsField int
 */
public void setOptionsField(int newOptionsField) {
	optionsField = newOptionsField;
}
/**
 * Insert the method's description here.
 * Creation date: (5/17/00 1:19:38 PM)
 * @param newRouteID long
 */
public void setRouteID(int newRouteID) {
	routeID = newRouteID;
}
/**
 * Insert the method's description here.
 * Creation date: (5/17/00 1:23:33 PM)
 * @param newTransmissionID long
 */
public void setTransmissionID(long newTransmissionID) {
	transmissionID = newTransmissionID;
}
/**
 * Insert the method's description here.
 * Creation date: (5/17/00 1:23:46 PM)
 * @param newUserMessageID long
 */
public void setUserMessageID(long newUserMessageID) {
	userMessageID = newUserMessageID;
}

public String toString() {
    ToStringCreator tsc = new ToStringCreator(this);
    tsc.append("deviceId", getDeviceID());
    tsc.append("command", getCommandString());
    tsc.append("userMessageId", getUserMessageID());
    return tsc.toString(); 
}
}
