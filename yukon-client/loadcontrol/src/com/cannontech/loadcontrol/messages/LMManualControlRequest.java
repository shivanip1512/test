package com.cannontech.loadcontrol.messages;

/**
 * ScheduleCommand objects are sent to the CBC server to request that an operation
 * be done on the given strategy.  Clients only send CBCCommands
 * and the server only receives them.
 */

public class LMManualControlRequest extends LMMessage 
{
	private int command;
	private int yukonID;
	private java.util.GregorianCalendar notifyTime = com.cannontech.common.util.CtiUtilities.get1990GregCalendar();
	private java.util.GregorianCalendar startTime = com.cannontech.common.util.CtiUtilities.get1990GregCalendar();
	private java.util.GregorianCalendar stopTime = com.cannontech.common.util.CtiUtilities.get1990GregCalendar();
	private int startGear = 0;
	private int startPriority = 0;
	private String addditionalInfo = new String();
	private boolean overrideConstraints = false;
	private boolean coerceStartStopTimes = false;
	
	/**
	 * @return Returns the coerceStartStopTimes.
	 */
	public boolean isCoerceStartStopTimes() {
		return coerceStartStopTimes;
	}
	/**
	 * @param coerceStartStopTimes The coerceStartStopTimes to set.
	 */
	public void setCoerceStartStopTimes(boolean coerceStartStopTimes) {
		this.coerceStartStopTimes = coerceStartStopTimes;
	}
	//The following are the different commands that
	//can be applied to control area, trigger, or program and map into the C++ side
  public static final int SCHEDULED_START = 0;
  public static final int SCHEDULED_STOP = 1;
  public static final int START_NOW = 2;
  public static final int STOP_NOW = 3;

	public static final String[] COMMAND_STRINGS =
	{
		"SCHEDULED START",
		"SCHEDULED STOP",
		"START NOW",
		"STOP NOW"
	};

/**
 * ScheduleCommand constructor comment.
 */
public LMManualControlRequest() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 3:41:37 PM)
 * @return java.lang.String
 */
public java.lang.String getAddditionalInfo() {
	return addditionalInfo;
}
/**
 * This method was created in VisualAge.
 * @return int
 */
public int getCommand() {
	return command;
}
/**
 * Insert the method's description here.
 * Creation date: (10/17/00 12:57:43 PM)
 * @return java.lang.String
 * @param command int
 */
public static String getCommandString(int command) 
{
	return COMMAND_STRINGS[command];
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 3:41:37 PM)
 * @return java.util.GregorianCalendar
 */
public java.util.GregorianCalendar getNotifyTime() {
	return notifyTime;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 3:41:37 PM)
 * @return int
 */
public int getStartGear() {
	return startGear;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 3:41:37 PM)
 * @return int
 */
public int getStartPriority() {
	return startPriority;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 3:41:37 PM)
 * @return java.util.GregorianCalendar
 */
public java.util.GregorianCalendar getStartTime() {
	return startTime;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 3:41:37 PM)
 * @return java.util.GregorianCalendar
 */
public java.util.GregorianCalendar getStopTime() {
	return stopTime;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 10:51:00 AM)
 * @return int
 */
public int getYukonID() {
	return yukonID;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 3:41:37 PM)
 * @param newAddditionalInfo java.lang.String
 */
public void setAddditionalInfo(java.lang.String newAddditionalInfo) {
	addditionalInfo = newAddditionalInfo;
}
/**
 * This method was created in VisualAge.
 * @param newValue int
 */
public void setCommand(int newValue) {
	this.command = newValue;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 3:41:37 PM)
 * @param newNotifyTime java.util.GregorianCalendar
 */
public void setNotifyTime(java.util.GregorianCalendar newNotifyTime) {
	notifyTime = newNotifyTime;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 3:41:37 PM)
 * @param newStartGear int
 */
public void setStartGear(int newStartGear) {
	startGear = newStartGear;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 3:41:37 PM)
 * @param newStartPriority int
 */
public void setStartPriority(int newStartPriority) {
	startPriority = newStartPriority;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 3:41:37 PM)
 * @param newStartTime java.util.GregorianCalendar
 */
public void setStartTime(java.util.GregorianCalendar newStartTime) {
	startTime = newStartTime;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 3:41:37 PM)
 * @param newStopTime java.util.GregorianCalendar
 */
public void setStopTime(java.util.GregorianCalendar newStopTime) {
	stopTime = newStopTime;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 10:51:00 AM)
 * @param newYukonID int
 */
public void setYukonID(int newYukonID) {
	yukonID = newYukonID;
}
	/**
	 * @return
	 */
	public boolean isOverrideConstraints() {
		return overrideConstraints;
	}

	/**
	 * @param b
	 */
	public void setOverrideConstraints(boolean b) {
		overrideConstraints = b;
	}

}
