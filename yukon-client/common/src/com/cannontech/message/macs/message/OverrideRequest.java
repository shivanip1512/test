package com.cannontech.message.macs.message;

/**
 * Insert the type's description here.
 * Creation date: (2/20/2001 4:49:30 PM)
 * @author: 
 */
import java.util.Date;

public class OverrideRequest extends com.cannontech.message.util.Message 
{
	// Correspond to the C++ enumeration
	public static final int OVERRIDE_START 		= 0;
	public static final int OVERRIDE_START_NOW 	= 1;	
	public static final int OVERRIDE_STOP 		= 2;
	public static final int OVERRIDE_STOP_NOW 	= 3;
	public static final int OVERRIDE_ENABLE 	= 4;
	public static final int OVERRIDE_DISABLE 	= 5;
	

	// action is either one of the above values
	private int action = -1;
	private long schedId = -1;
	private Date start = new java.sql.Date(0);
	private Date stop = new java.sql.Date(0);
/**
 * OverrrideRequest constructor comment.
 */
public OverrideRequest() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (2/20/2001 4:53:35 PM)
 * @return int
 */
public int getAction() {
	return action;
}
/**
 * Insert the method's description here.
 * Creation date: (2/20/2001 4:53:35 PM)
 * @return long
 */
public long getSchedId() {
	return schedId;
}
/**
 * Insert the method's description here.
 * Creation date: (2/20/2001 4:53:35 PM)
 * @return java.util.Date
 */
public java.util.Date getStart() {
	return start;
}
/**
 * Insert the method's description here.
 * Creation date: (2/20/2001 4:53:35 PM)
 * @return java.util.Date
 */
public java.util.Date getStop() {
	return stop;
}
/**
 * Insert the method's description here.
 * Creation date: (2/20/2001 4:53:35 PM)
 * @param newAction long
 */
public void setAction(int newAction) {
	action = newAction;
}
/**
 * Insert the method's description here.
 * Creation date: (2/20/2001 4:53:35 PM)
 * @param newSchedId long
 */
public void setSchedId(long newSchedId) {
	schedId = newSchedId;
}
/**
 * Insert the method's description here.
 * Creation date: (2/20/2001 4:53:35 PM)
 * @param newStart java.util.Date
 */
public void setStart(java.util.Date newStart) {
	start = newStart;
}
/**
 * Insert the method's description here.
 * Creation date: (2/20/2001 4:53:35 PM)
 * @param newStop java.util.Date
 */
public void setStop(java.util.Date newStop) {
	stop = newStop;
}
}
