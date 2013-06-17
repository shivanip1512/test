package com.cannontech.message.macs.message;

/**
 * Insert the type's description here.
 * Creation date: (2/20/2001 5:36:49 PM)
 * @author: 
 */
public class DeleteSchedule extends com.cannontech.message.util.Message 
{
	private long id;
/**
 * RetrieveSchedule constructor comment.
 */
public DeleteSchedule() {
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
