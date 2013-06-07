package com.cannontech.messaging.message.porter;

import java.util.Vector;

import org.springframework.core.style.ToStringCreator;

import com.cannontech.messaging.message.BaseMessage;

public class ReturnMessage extends BaseMessage {

    private int deviceId;
    private String commandString;
    private String resultString;
    private int status;
    private int routeOffset;
    private int macroOffset;
    private int attemptNum;
    private boolean expectMore;
    private long groupMessageId;
    private long userMessageId;
    private Vector vector;

    public int getAttemptNum() {
        return attemptNum;
    }

    public java.lang.String getCommandString() {
        return commandString;
    }

    public int getDeviceId() {
        return deviceId;
    }

    public boolean getExpectMore() {
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

    public long getGroupMessageId() {
        return groupMessageId;
    }

    public long getUserMessageId() {
        return userMessageId;
    }

    public void setAttemptNum(int newAttemptNum) {
        attemptNum = newAttemptNum;
    }

    public void setCommandString(java.lang.String newCommandString) {
        commandString = newCommandString;
    }

    public void setDeviceId(int newDeviceId) {
        deviceId = newDeviceId;
    }

    public void setExpectMore(boolean newExpectMore) {
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

    public void setGroupMessageId(long newGroupMessageId) {
        groupMessageId = newGroupMessageId;
    }

    public void setUserMessageId(long newUserMessageId) {
        userMessageId = newUserMessageId;
    }

    public Vector getVector() {
        if (vector == null) {
            vector = new Vector();
        }
        return vector;
    }

    public void setVector(Vector newVector) {
        vector = newVector;
    }

    public String toString() {
        ToStringCreator tsc = new ToStringCreator(this);
        tsc.append("deviceId", getDeviceId());
        tsc.append("command", getCommandString());
        tsc.append("result", getResultString());
        tsc.append("userMessageId", getUserMessageId());
        tsc.append("status", getStatus());
        tsc.append("expectMore", getExpectMore());
        tsc.append("points", getVector());
        return tsc.toString();
    }
}
