package com.cannontech.message.util;

/**
 * Message is the base message type for all messages.  It corresponds to the C++ base message class CtiMessage
 */
import java.util.Date;

import com.cannontech.common.util.CtiUtilities;

public class Message {
    
	private Date timeStamp = null;
	private int	priority = 0;
	private int SOE_Tag = 0;
	private String userName = CtiUtilities.getUserName();
	private int token = 0;
	private String source = null;
    	
    public Message() {
    	super();
    	timeStamp = new Date();
    }
    
    public int getPriority() {
    	return priority;
    }
    
    public int getSOE_Tag() {
    	return SOE_Tag;
    }
    
    public String getSource() {
    	if(source == null) {
    		source = CtiUtilities.DEFAULT_MSG_SOURCE;
    	}
    	return source;
    }
    
    public Date getTimeStamp() {
    	return timeStamp;
    }
    
    public int getToken() {
    	return token;
    }
    
    public String getUserName() {
    	return userName;
    }
    
    public void setPriority(int priority) {
    	this.priority = priority;
    }
    
    public void setSOE_Tag(int newSOE_Tag) {
    	SOE_Tag = newSOE_Tag;
    }
    
    public void setSource(String newSource) {
    	source = newSource;
    }
    
    public void setTimeStamp(Date timeStamp) {
    	this.timeStamp = timeStamp;
    }
    
    public void setToken(int newToken) {
    	token = newToken;
    }
    
    public void setUserName(String newUserName) {
    	userName = newUserName;
    }
    
    @Override
    public String toString() {
        return String
            .format("Message [timeStamp=%s, priority=%s, SOE_Tag=%s, userName=%s, token=%s, source=%s]",
                    timeStamp,
                    priority,
                    SOE_Tag,
                    userName,
                    token,
                    source);
    }

}