package com.cannontech.message.macs.message;

/**
 * Insert the type's description here.
 * Creation date: (2/20/2001 5:14:33 PM)
 * @author: 
 */
public class RetrieveScript extends com.cannontech.message.util.Message {

	// The name of the script
	private String scriptName;
/**
 * RetrieveScript constructor comment.
 */
public RetrieveScript() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (2/20/2001 5:15:00 PM)
 * @return java.lang.String
 */
public java.lang.String getScriptName() {
	return scriptName;
}
/**
 * Insert the method's description here.
 * Creation date: (2/20/2001 5:15:00 PM)
 * @param newName java.lang.String
 */
public void setScriptName(java.lang.String newName) {
	scriptName = newName;
}
}
