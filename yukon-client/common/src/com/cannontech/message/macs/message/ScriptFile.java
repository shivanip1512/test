package com.cannontech.message.macs.message;

/**
 * Insert the type's description here.
 * Creation date: (3/9/2001 11:40:53 AM)
 * @author: 
 */
public class ScriptFile extends com.cannontech.message.util.Message 
{
	private String fileName = "";
	private String fileContents = "";
/**
 * ScriptFile constructor comment.
 */
public ScriptFile() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (3/9/2001 11:41:25 AM)
 * @return java.lang.String
 */
public java.lang.String getFileContents() {
	return fileContents;
}
/**
 * Insert the method's description here.
 * Creation date: (3/9/2001 11:41:25 AM)
 * @return java.lang.String
 */
public java.lang.String getFileName() {
	return fileName;
}
/**
 * Insert the method's description here.
 * Creation date: (3/9/2001 11:41:25 AM)
 * @param newFileContents java.lang.String
 */
public void setFileContents(java.lang.String newFileContents) {
	fileContents = newFileContents;
}
/**
 * Insert the method's description here.
 * Creation date: (3/9/2001 11:41:25 AM)
 * @param newFileName java.lang.String
 */
public void setFileName(java.lang.String newFileName) {
	fileName = newFileName;
}
}
