package com.cannontech.message.porter.message;

import com.cannontech.message.util.Message;

public class Request extends Message {
    
    private int deviceID;
    private String commandString = "";
    private int routeID;
    private int macroOffset;
    private int attemptNum;
    private long groupMessageID;
    private long userMessageID;
    private int optionsField;
    
    public Request() { super(); }
    
    public Request(int deviceID, String commandString, long userMessageID) {
        this.deviceID = deviceID;
        this.commandString = commandString;
        this.userMessageID = userMessageID;
    }
    
    public int getDeviceID() {
        return deviceID;
    }
    
    public void setDeviceID(int deviceID) {
        this.deviceID = deviceID;
    }
    
    public String getCommandString() {
        return commandString;
    }
    
    public void setCommandString(String commandString) {
        this.commandString = commandString;
    }
    
    public int getRouteID() {
        return routeID;
    }
    
    public void setRouteID(int routeID) {
        this.routeID = routeID;
    }
    
    public int getMacroOffset() {
        return macroOffset;
    }
    
    public void setMacroOffset(int macroOffset) {
        this.macroOffset = macroOffset;
    }
    
    public int getAttemptNum() {
        return attemptNum;
    }
    
    public void setAttemptNum(int attemptNum) {
        this.attemptNum = attemptNum;
    }
    
    public long getGroupMessageID() {
        return groupMessageID;
    }
    
    public void setGroupMessageID(long groupMessageID) {
        this.groupMessageID = groupMessageID;
    }
    
    public long getUserMessageID() {
        return userMessageID;
    }
    
    public void setUserMessageID(long userMessageID) {
        this.userMessageID = userMessageID;
    }
    
    public int getOptionsField() {
        return optionsField;
    }
    
    public void setOptionsField(int optionsField) {
        this.optionsField = optionsField;
    }
    
    @Override
    public String toString() {
        return String.format("Request [deviceID=%s, commandString=%s, routeID=%s, macroOffset=%s, attemptNum=%s, groupMessageID=%s, userMessageID=%s, optionsField=%s]", deviceID, commandString,
                routeID, macroOffset, attemptNum, groupMessageID, userMessageID, optionsField);
    }
    
}