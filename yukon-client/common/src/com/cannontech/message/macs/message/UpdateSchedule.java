package com.cannontech.message.macs.message;

/**
 * Insert the type's description here.
 * Creation date: (2/20/2001 6:42:50 PM)
 * @author: 
 */

public class UpdateSchedule extends com.cannontech.message.util.Message 
{
	// The schedule to update
	private Schedule schedule;

	// The schedules script
	private String script = "";
	
/**
 * UpdateSchedule constructor comment.
 */
public UpdateSchedule() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (2/27/2001 9:53:46 AM)
 * @return Schedule
 */
public Schedule getSchedule() {
	return schedule;
}
/**
 * Insert the method's description here.
 * Creation date: (2/20/2001 6:43:33 PM)
 * @return java.lang.String
 */
public java.lang.String getScript() {
	return script;
}
/**
 * Insert the method's description here.
 * Creation date: (2/27/2001 9:53:46 AM)
 * @param newSchedule Schedule
 */
public void setSchedule(Schedule newSchedule) {
	schedule = newSchedule;
}
/**
 * Insert the method's description here.
 * Creation date: (2/20/2001 6:43:33 PM)
 * @param newScript java.lang.String
 */
public void setScript(java.lang.String newScript) {
	script = newScript;
}
}
