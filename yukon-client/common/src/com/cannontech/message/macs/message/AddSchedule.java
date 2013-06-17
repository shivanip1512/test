package com.cannontech.message.macs.message;

/**
 * Insert the type's description here.
 * Creation date: (2/20/2001 6:41:10 PM)
 * @author: 
 */
public class AddSchedule extends com.cannontech.message.util.Message 
{
	//The schedule to add
	private Schedule schedule;

	// The script for the schedule if any
	private String script = "";
/**
 * AddSchedule constructor comment.
 */
public AddSchedule() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (2/27/2001 9:44:57 AM)
 * @return Schedule
 */
public Schedule getSchedule() {
	return schedule;
}
/**
 * Insert the method's description here.
 * Creation date: (2/20/2001 6:42:26 PM)
 * @return java.lang.String
 */
public java.lang.String getScript() {
	return script;
}
/**
 * Insert the method's description here.
 * Creation date: (2/27/2001 9:44:57 AM)
 * @param newSchedule Schedule
 */
public void setSchedule(Schedule newSchedule) {
	schedule = newSchedule;
}
/**
 * Insert the method's description here.
 * Creation date: (2/20/2001 6:42:26 PM)
 * @param newScript java.lang.String
 */
public void setScript(java.lang.String newScript) {
	script = newScript;
}
}
