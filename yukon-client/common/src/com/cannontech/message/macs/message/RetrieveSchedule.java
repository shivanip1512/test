package com.cannontech.message.macs.message;

/**
 * Insert the type's description here.
 * Creation date: (2/20/2001 5:36:49 PM)
 * @author: 
 */
public class RetrieveSchedule extends com.cannontech.message.util.Message 
{
	public static long ALL_SCHEDULES = 0;
	private long id;
/**
 * RetrieveSchedule constructor comment.
 */
public RetrieveSchedule() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (2/20/2001 5:37:12 PM)
 * @return long
 */
public long getScheduleId() {
	return id;
}
/**
 * Insert the method's description here.
 * Creation date: (2/20/2001 5:37:12 PM)
 * @param newId long
 */
public void setScheduleId(long newId) {
	id = newId;
}
}
