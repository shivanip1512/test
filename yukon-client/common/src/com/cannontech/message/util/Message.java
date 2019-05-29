package com.cannontech.message.util;

import java.io.Serializable;
import java.util.Date;

import com.cannontech.common.util.CtiUtilities;

/**
 * Message is the base message type for all messages.  
 * It corresponds to the C++ base message class CtiMessage.
 */
public class Message implements Serializable {
    
    private static final long serialVersionUID = -5133067389097578007L;
    
    private Date timeStamp;
    private int priority = 7;
    private int soeTag;
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
    
    public int getSoeTag() {
        return soeTag;
    }
    
    public void setSoeTag(int soeTag) {
        this.soeTag = soeTag;
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
        return String.format("Message [timeStamp=%s, priority=%s, soeTag=%s, userName=%s, source=%s]", 
                timeStamp, priority, soeTag, userName, source);
    }
    
}