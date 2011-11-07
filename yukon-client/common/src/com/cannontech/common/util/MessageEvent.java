package com.cannontech.common.util;

import java.util.Date;
import java.util.EventObject;

/**
 * This type represents system events.
 */
public class MessageEvent extends EventObject {
	private String message = null;
	private Date creationDate = null;
	private int messageType = 0;
	public static final int INFORMATION_MESSAGE = 0;
	public static final int ERROR_MESSAGE = 1;
	
/**
 * SystemEvent constructor comment.
 * @param source java.lang.Object
 */
public MessageEvent(Object source) {
	super(source);
	initialize(null);
}
/**
 * SystemEvent constructor comment.
 * @param source java.lang.Object
 */
public MessageEvent(Object source, String message) {
	super(source);
	initialize(message);
}
/**
 * This method was created by Cannon Technologies Inc.
 */
public MessageEvent(Object source, String message, int type) {
	super(source);
	initialize(message,type);
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String getMessage() {
	return message;
}
/**
 * This method was created by Cannon Technologies Inc.
 * @return java.lang.Integer
 */
public int getMessageType() {
	return messageType;
}
/**
 * This method was created in VisualAge.
 * @param message java.lang.String
 */
private void initialize(String message) {

	creationDate = new Date();
	setMessage(message);		
}
/**
 * This method was created in VisualAge.
 * @param message java.lang.String
 */
private void initialize(String message, int type) {

	creationDate = new Date();
	setMessage(message);
	setMessageType(type);
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.String
 */
public void setMessage(String newValue) {
	this.message = newValue;
}
/**
 * This method was created by Cannon Technologies Inc.
 * @param typeNum int
 */
public void setMessageType(int typeNum) {
	this.messageType = typeNum;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String toString() {
	return creationDate + " - " + getMessage();
}
}
