package com.cannontech.message.porter.message;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.message.util.Message;

public class Return extends Message {
    
    private int deviceID;
    private String commandString;
    private String resultString;
    private int status;
    private int routeOffset;
    private int macroOffset;
    private int attemptNum;
    private int expectMore;
    private long groupMessageID;
    private long userMessageID;
    private List<Message> messages;
    
    public int getAttemptNum() {
        return attemptNum;
    }
    
    public String getCommandString() {
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
    
    public String getResultString() {
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
    
    public void setAttemptNum(int attemptNum) {
        this.attemptNum = attemptNum;
    }
    
    public void setCommandString(String commandString) {
        this.commandString = commandString;
    }
    
    public void setDeviceID(int deviceId) {
        this.deviceID = deviceId;
    }
    
    public void setExpectMore(int expectMore) {
        this.expectMore = expectMore;
    }
    
    public void setMacroOffset(int macroOffset) {
        this.macroOffset = macroOffset;
    }
    
    public void setResultString(String resultString) {
        this.resultString = resultString;
    }
    
    public void setRouteOffset(int routeOffset) {
        this.routeOffset = routeOffset;
    }
    
    public void setStatus(int status) {
        this.status = status;
    }
    
    public void setGroupMessageID(long groupMessageID) {
        this.groupMessageID = groupMessageID;
    }
    
    public void setUserMessageID(long userMessageID) {
        this.userMessageID = userMessageID;
    }
    
    public List<Message> getMessages() {
        if (messages == null) {
            messages = new ArrayList<>();
        }
        
        return messages;
    }
    
    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }
    
    @Override
    public String toString() {
        return String.format(
                "Return [deviceID=%s, commandString=%s, resultString=%s, status=%s, routeOffset=%s, macroOffset=%s, attemptNum=%s, expectMore=%s, groupMessageID=%s, userMessageID=%s, vector=%s]",
                deviceID, commandString, resultString, status, routeOffset, macroOffset, attemptNum, expectMore, groupMessageID, userMessageID, messages);
    }
    
}