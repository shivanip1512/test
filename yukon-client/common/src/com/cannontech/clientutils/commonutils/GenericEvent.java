package com.cannontech.clientutils.commonutils;

/**
 * This type represents system events.
 */
public class GenericEvent extends java.util.EventObject 
{
	private String message = null;
	private java.util.Date creationDate = null;
	private int eventId = 0;
	
	public static final int DEFAULT_GENERIC_EVENT = 10000;
	//public static final int ERROR_MESSAGE = 1;
/**
 * SystemEvent constructor comment.
 * @param source java.lang.Object
 */
public GenericEvent(Object source) {
	super(source);
	initialize(null);
}
/**
 * SystemEvent constructor comment.
 * @param source java.lang.Object
 */
public GenericEvent(Object source, String message) {
	super(source);
	initialize(message);
}
/**
 * This method was created by Cannon Technologies Inc.
 */
public GenericEvent(Object source, String message, int type) {
	super(source);
	initialize(message,type);
}
/**
 * Insert the method's description here.
 * Creation date: (2/22/2001 2:22:47 PM)
 * @return int
 */
public int getEventId() {
	return eventId;
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
 * @param message java.lang.String
 */
private void initialize(String message) {

	creationDate = new java.util.Date();
	setMessage(message);		
}
/**
 * This method was created in VisualAge.
 * @param message java.lang.String
 */
private void initialize(String message, int id) {

	creationDate = new java.util.Date();
	setMessage(message);
	setEventId(id);
}
/**
 * Insert the method's description here.
 * Creation date: (2/22/2001 2:22:47 PM)
 * @param newEventId int
 */
public void setEventId(int newEventId) {
	eventId = newEventId;
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.String
 */
public void setMessage(String newValue) {
	this.message = newValue;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String toString() {
	return creationDate + " - " + getMessage();
}
}
