package com.cannontech.loadcontrol.messages;

import java.util.GregorianCalendar;

import org.springframework.core.style.ToStringCreator;

import com.cannontech.common.util.CtiUtilities;

/**
 * ScheduleCommand objects are sent to the CBC server to request that an operation
 * be done on the given strategy.  Clients only send CBCCommands
 * and the server only receives them.
 */

public class LMManualControlRequest extends LMMessage 
{
	private int command;
	private int yukonID;
	private GregorianCalendar notifyTime = CtiUtilities.get1990GregCalendar();
	private GregorianCalendar startTime = CtiUtilities.get1990GregCalendar();
	private GregorianCalendar stopTime = CtiUtilities.get1990GregCalendar();
	private int startGear = 0;
	private int startPriority = 0;
	private String addditionalInfo = new String();
	private int constraintFlag = CONSTRAINTS_FLAG_USE;
    private String originSource;

	//The following are the different commands that
	//can be applied to control area, trigger, or program and map into the C++ side
	public static final int SCHEDULED_START = 0;
	public static final int SCHEDULED_STOP = 1;
	public static final int START_NOW = 2;
	public static final int STOP_NOW = 3;
    public static final int CHANGE_GEAR = 4;

	public static final String[] COMMAND_STRINGS =
	{
		"SCHEDULED START",
		"SCHEDULED STOP",
		"START NOW",
		"STOP NOW",
        "CHANGE GEAR"
	};

	//LMProgram constraint flags
	public static final int CONSTRAINTS_FLAG_USE = 0;
	public static final int CONSTRAINTS_FLAG_OVERRIDE = 1;
	public static final int CONSTRAINTS_FLAG_CHECK = 2;

	public static final String[] CONSTRAINT_FLAG_STRS = {
		"Observe",
		"Override",
		"Check"
	};

/**
 * constructor comment.
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
 * Returns the Constraint Flag ID given the String representation
 */
public static int getConstraintID( String constStr ) 
{
	for( int i = 0; i < CONSTRAINT_FLAG_STRS.length; i++ ) {		
		if( constStr.equalsIgnoreCase(CONSTRAINT_FLAG_STRS[i]) )
			return i;
	}

	//some reasonable default value
	return CONSTRAINTS_FLAG_USE;
}

/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 3:41:37 PM)
 * @return GregorianCalendar
 */
public GregorianCalendar getNotifyTime() {
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
 * @return GregorianCalendar
 */
public GregorianCalendar getStartTime() {
	return startTime;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 3:41:37 PM)
 * @return GregorianCalendar
 */
public GregorianCalendar getStopTime() {
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
 * @param newNotifyTime GregorianCalendar
 */
public void setNotifyTime(GregorianCalendar newNotifyTime) {
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
 * @param newStartTime GregorianCalendar
 */
public void setStartTime(GregorianCalendar newStartTime) {
	startTime = newStartTime;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 3:41:37 PM)
 * @param newStopTime GregorianCalendar
 */
public void setStopTime(GregorianCalendar newStopTime) {
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
	public int getConstraintFlag() {
		return constraintFlag;
	}

	/**
	 * @param i
	 */
	public void setConstraintFlag(int i) {
		constraintFlag = i;
	}

    public String getOriginSource() {
        return originSource;
    }

    public void setOriginSource(String originSource) {
        this.originSource = originSource;
    }

	public String toString() {
	    ToStringCreator tsc = new ToStringCreator(this);
	    tsc.append("command", getCommandString(getCommand()));
	    tsc.append("yukonID", getYukonID());
	    tsc.append("notifyTime", getNotifyTime().getTime());
	    tsc.append("startTime", getStartTime().getTime());
	    tsc.append("stopTime", getStopTime().getTime());
	    tsc.append("startGear", getStartGear());
	    tsc.append("startPriority", getStartPriority());
	    tsc.append("addditionalInfo", getAddditionalInfo());
	    tsc.append("constraintFlag", CONSTRAINT_FLAG_STRS[getConstraintFlag()]);
	    tsc.append("originSource", getOriginSource());
	    return tsc.toString(); 
	}

}
