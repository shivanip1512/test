package com.cannontech.message.porter.message;

import org.springframework.core.style.ToStringCreator;

public class Request extends com.cannontech.message.util.Message 
{
	private int deviceID = 0;
	private java.lang.String commandString = "";
	private int routeID = 0;	
	private int macroOffset = 0;
	private int attemptNum = 0;
	private long groupMessageID = 0;
	private long userMessageID = 0;
	private int optionsField = 0;
/**
 * Request constructor comment.
 */
public Request() {
	super();
}

public Request(int deviceID, String commandString, long userMessageID) 
{
	setDeviceID(deviceID);
	setCommandString(commandString);
	setUserMessageID(userMessageID);
}

public int getAttemptNum() {
	return attemptNum;
}

public java.lang.String getCommandString() {
	return commandString;
}

public int getDeviceID() {
	return deviceID;
}

public int getMacroOffset() {
	return macroOffset;
}

public int getOptionsField() {
	return optionsField;
}

public int getRouteID() {
	return routeID;
}

public long getGroupMessageID() {
	return groupMessageID;
}

public long getUserMessageID() {
	return userMessageID;
}

public void setAttemptNum(int newAttemptNum) {
	attemptNum = newAttemptNum;
}

public void setCommandString(java.lang.String newCommandString) {
	commandString = newCommandString;
}

public void setDeviceID(int newDeviceID) {
	deviceID = newDeviceID;
}

public void setMacroOffset(int newMacroOffset) {
	macroOffset = newMacroOffset;
}

public void setOptionsField(int newOptionsField) {
	optionsField = newOptionsField;
}

public void setRouteID(int newRouteID) {
	routeID = newRouteID;
}

public void setGroupMessageID(long newGroupMessageID) {
	groupMessageID = newGroupMessageID;
}

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
