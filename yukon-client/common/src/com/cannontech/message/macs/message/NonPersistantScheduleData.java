package com.cannontech.message.macs.message;

/**
 * Insert the type's description here.
 * Creation date: (3/8/2001 2:01:44 PM)
 * @author: 
 */
public class NonPersistantScheduleData
{
	private com.cannontech.message.macs.message.ScriptFile script = null;
	private java.util.Enumeration categories = null;
/**
 * NonPersistantScheduleData constructor comment.
 */
public NonPersistantScheduleData() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (3/8/2001 2:13:09 PM)
 * @return java.util.Enumeration
 */
public java.util.Enumeration getCategories() {
	return categories;
}
/**
 * Insert the method's description here.
 * Creation date: (3/9/2001 1:34:19 PM)
 * @return com.cannontech.message.macs.message.ScriptFile
 */
public com.cannontech.message.macs.message.ScriptFile getScript() 
{
	if( script == null )
		script = new com.cannontech.message.macs.message.ScriptFile();
		
	return script;
}
/**
 * Insert the method's description here.
 * Creation date: (3/8/2001 2:13:09 PM)
 * @param newCategories java.util.Enumeration
 */
public void setCategories(java.util.Enumeration newCategories) {
	categories = newCategories;
}
/**
 * Insert the method's description here.
 * Creation date: (3/9/2001 1:34:19 PM)
 * @param newScript com.cannontech.message.macs.message.ScriptFile
 */
public void setScript(com.cannontech.message.macs.message.ScriptFile newScript) {
	script = newScript;
}
}
