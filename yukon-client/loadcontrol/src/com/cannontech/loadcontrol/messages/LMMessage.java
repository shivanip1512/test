package com.cannontech.loadcontrol.messages;

/**
 * This type was created in VisualAge.
 */
public class LMMessage extends com.cannontech.message.util.Message
{
	private String message = new String();
/**
 * Message constructor comment.
 */
public LMMessage() {
	super();	
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String getMessage() {
	return message;
}
/**
 * This method was created in VisualAge.
 */
public void setMessage(String message) {
	this.message = message;
}
}
