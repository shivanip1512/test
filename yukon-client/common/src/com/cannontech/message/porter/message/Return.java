package com.cannontech.message.porter.message;

import java.util.Vector;

import org.springframework.core.style.ToStringCreator;

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
	private long groupMessageID;
	private long userMessageID;
	
	
	private Vector vector = null;
	
/**
 * Return constructor comment.
 */
public Return() {
	super();
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

public int getExpectMore() {
	return expectMore;
}

public int getMacroOffset() {
	return macroOffset;
}

public java.lang.String getResultString() {
	return resultString;
}

public int getRouteOffset() {
	return routeOffset;
}

public int getStatus() {
	return status;
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

public void setExpectMore(int newExpectMore) {
	expectMore = newExpectMore;
}

public void setMacroOffset(int newMacroOffset) {
	macroOffset = newMacroOffset;
}

public void setResultString(java.lang.String newResultString) {
	resultString = newResultString;
}

public void setRouteOffset(int newRouteOffset) {
	routeOffset = newRouteOffset;
}

public void setStatus(int newStatus) {
	status = newStatus;
}

public void setGroupMessageID(long newGroupMessageID) {
	groupMessageID = newGroupMessageID;
}

public void setUserMessageID(long newUserMessageID) {
	userMessageID = newUserMessageID;
}

public java.util.Vector getVector() {
	if( vector == null )
	{
		vector = new Vector();
	}
	
	return vector;
}

public void setVector(java.util.Vector newVector) {
	vector = newVector;
}

public String toString() {
    ToStringCreator tsc = new ToStringCreator(this);
    tsc.append("deviceId", getDeviceID());
    tsc.append("command", getCommandString());
    tsc.append("result", getResultString());
    tsc.append("userMessageId", getUserMessageID());
    tsc.append("status", getStatus());
    tsc.append("expectMore", getExpectMore());
    tsc.append("points", getVector());
    return tsc.toString(); 
}
}