package com.cannontech.common.util;

/**
 * This type represents system events.
 */
public class SystemEvent extends java.util.EventObject {
	private String message = null;
	private java.util.Date creationDate = null;
/**
 * SystemEvent constructor comment.
 * @param source java.lang.Object
 */
public SystemEvent(Object source) {
	super(source);
	initialize(null);
}
/**
 * SystemEvent constructor comment.
 * @param source java.lang.Object
 */
public SystemEvent(Object source, String message) {
	super(source);
	initialize(message);
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
