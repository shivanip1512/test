package com.cannontech.messaging.message.porter;

import org.springframework.core.style.ToStringCreator;

import com.cannontech.messaging.message.BaseMessage;

public class RequestMessage extends BaseMessage {

    private int deviceId = 0;
    private java.lang.String commandString = "";
    private int routeId = 0;
    private int macroOffset = 0;
    private int attemptNum = 0;
    private long groupMessageId = 0;
    private long userMessageId = 0;
    private int optionsField = 0;

    public RequestMessage() {
        super();
    }

    public RequestMessage(int deviceId, String commandString, long userMessageId) {
        setDeviceId(deviceId);
        setCommandString(commandString);
        setUserMessageId(userMessageId);
    }

    public int getAttemptNum() {
        return attemptNum;
    }

    public java.lang.String getCommandString() {
        return commandString;
    }

    public int getDeviceId() {
        return deviceId;
    }

    public int getMacroOffset() {
        return macroOffset;
    }

    public int getOptionsField() {
        return optionsField;
    }

    public int getRouteId() {
        return routeId;
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

    public void setMacroOffset(int newMacroOffset) {
        macroOffset = newMacroOffset;
    }

    public void setOptionsField(int newOptionsField) {
        optionsField = newOptionsField;
    }

    public void setRouteId(int newRouteId) {
        routeId = newRouteId;
    }

    public void setGroupMessageId(long newGroupMessageId) {
        groupMessageId = newGroupMessageId;
    }

    public void setUserMessageId(long newUserMessageId) {
        userMessageId = newUserMessageId;
    }

    public String toString() {
        ToStringCreator tsc = new ToStringCreator(this);
        tsc.append("deviceId", getDeviceId());
        tsc.append("command", getCommandString());
        tsc.append("userMessageId", getUserMessageId());
        return tsc.toString();
    }
}
