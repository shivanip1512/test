package com.cannontech.message.util;

/**
 * Message is the base message type for all messages.  It corresponds to the C++ base message class CtiMessage
 */
import java.util.Date;

public class Message 
{	
	private Date timeStamp = null;
	private int	priority = 0;
	private int SOE_Tag = 0;
	private String userName = com.cannontech.common.util.CtiUtilities.getUserName();
	private int token = 0;
	private String source = null;
/**
 * Message constructor comment.
 */
public Message() {
	super();
	timeStamp = new java.util.Date();
}
/**
 * This method was created in VisualAge.
 * @return int
 */
public int getPriority() {
	return priority;
}
/**
 * Insert the method's description here.
 * Creation date: (1/25/2001 1:22:54 PM)
 * @return int
 */
public int getSOE_Tag() {
	return SOE_Tag;
}
/**
 * Insert the method's description here.
 * Creation date: (12/14/2001 2:27:56 PM)
 * @return java.lang.String
 */
public java.lang.String getSource()
{
	if( source == null )
		source = com.cannontech.common.util.CtiUtilities.DEFAULT_MSG_SOURCE;
	
	return source;
}
/**
 * This method was created in VisualAge.
 * @return java.util.Date
 */
public java.util.Date getTimeStamp() {
	return timeStamp;
}
/**
 * Insert the method's description here.
 * Creation date: (1/25/2001 4:34:31 PM)
 * @return int
 */
public int getToken() {
	return token;
}
/**
 * Insert the method's description here.
 * Creation date: (1/25/2001 4:34:31 PM)
 * @return java.lang.String
 */
public java.lang.String getUserName() 
{
	return userName;
}
/**
 * This method was created in VisualAge.
 * @param priority int
 */
public void setPriority(int priority) {
	this.priority = priority;
}
/**
 * Insert the method's description here.
 * Creation date: (1/25/2001 1:22:54 PM)
 * @param newSOE_Tag int
 */
public void setSOE_Tag(int newSOE_Tag) {
	SOE_Tag = newSOE_Tag;
}
/**
 * Insert the method's description here.
 * Creation date: (12/14/2001 2:27:56 PM)
 * @param newSource java.lang.String
 */
public void setSource(java.lang.String newSource) {
	source = newSource;
}
/**
 * This method was created in VisualAge.
 * @param timeStamp java.util.Date
 */
public void setTimeStamp(java.util.Date timeStamp) {
	this.timeStamp = timeStamp;
}
/**
 * Insert the method's description here.
 * Creation date: (1/25/2001 4:34:31 PM)
 * @param newToken int
 */
public void setToken(int newToken) {
	token = newToken;
}
/**
 * Insert the method's description here.
 * Creation date: (1/25/2001 4:34:31 PM)
 * @param newUserName java.lang.String
 */
public void setUserName(java.lang.String newUserName) {
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
