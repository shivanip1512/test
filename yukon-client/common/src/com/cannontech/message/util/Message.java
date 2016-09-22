package com.cannontech.message.util;

import java.util.Date;

import com.cannontech.common.util.CtiUtilities;

/**
 * Message is the base message type for all messages.  
 * It corresponds to the C++ base message class CtiMessage.
 */
public class Message {
    
    private Date timeStamp;
    private int priority;
    private int SOE_Tag;
    private String userName = CtiUtilities.getUserName();
    private String source = CtiUtilities.DEFAULT_MSG_SOURCE;
    
    public Message() {
        super();
        timeStamp = new Date();
    }
    
    public Date getTimeStamp() {
        return timeStamp;
    }
    
    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }
    
    public int getPriority() {
        return priority;
    }
    
    public void setPriority(int priority) {
        this.priority = priority;
    }
    
    public int getSOE_Tag() {
        return SOE_Tag;
    }
    
    public void setSOE_Tag(int sOE_Tag) {
        SOE_Tag = sOE_Tag;
    }
    
    public String getUserName() {
        return userName;
    }
    
    public void setUserName(String userName) {
        this.userName = userName;
    }
    
    public String getSource() {
        return source;
    }
    
    public void setSource(String source) {
        this.source = source;
    }
    
    @Override
    public String toString() {
        return String.format("Message [timeStamp=%s, priority=%s, SOE_Tag=%s, userName=%s, source=%s]", 
                timeStamp, priority, SOE_Tag, userName, source);
    }
    
}