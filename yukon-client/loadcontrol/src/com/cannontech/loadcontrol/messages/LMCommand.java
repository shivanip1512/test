package com.cannontech.loadcontrol.messages;

/**
 * ScheduleCommand objects are sent to the CBC server to request that an operation
 * be done on the given strategy.  Clients only send CBCCommands
 * and the server only receives them.
 */

public class LMCommand extends LMMessage 
{
	private int command;
	private int yukonID = 0;
	private int number = 0;
	private double value = 0.0;
	private int count = 0;
	private int auxid = 0;


	//The following are the different commands that
	//can be applied to control area, trigger, or program and map into the C++ side
	public static final int CHANGE_THRESHOLD							= 0;
	public static final int CHANGE_RESTORE_OFFSET					= 1;
	public static final int CHANGE_CURRENT_START_TIME				= 2;
	public static final int CHANGE_CURRENT_STOP_TIME				= 3;
	public static final int CHANGE_CURRENT_OPERATIONAL_STATE		= 4;
	public static final int ENABLE_CONTROL_AREA						= 5;
	public static final int DISABLE_CONTROL_AREA						= 6;
	public static final int ENABLE_PROGRAM								= 7;
	public static final int DISABLE_PROGRAM 							= 8;
	public static final int RETRIEVE_ALL_CONTROL_AREAS				= 9;
	  
	public static final int SHED_GROUP									= 10;
	public static final int SMART_CYCLE_GROUP							= 11;
	public static final int TRUE_CYCLE_GROUP							= 12;
	public static final int RESTORE_GROUP								= 13;
	public static final int ENABLE_GROUP								= 14;
	public static final int DISABLE_GROUP								= 15;
	public static final int CONFIRM_GROUP								= 16;
	public static final int RESET_PEAK_POINT_VALUE                      = 17;
	public static final int EMERGENCY_DISABLE_PROGRAM					= 18;
          
	public static final String[] CMD_STRS =
	{
		"CHANGE THRESHOLD",   //0
		"CHANGE RESTORE OFFSET",
		"CHANGE CURRENT START TIME",
		"CHANGE CURRENT STOP TIME",
		"CHANGE CURRENT OPERATIONAL STATE",
		"ENABLE CONTROL AREA",
		"DISABLE CONTROL AREA", //6
		
		"Enable Program",
		"Disable Program",
		"Retrieve All Areas", //9
		
		"Shed",
		"Smart Cycle",
		"True Cycle",
		"Restore",
		"Enable Group",  //14
		"Disable Group",
		"Confirm",
		"Reset Peak Point",
		"Emergency Disable Program"
	};
/**
 * ScheduleCommand constructor comment.
 */
public LMCommand() {
	super();
}
/**
 * LMCommand constructor comment.
 */
public LMCommand( int cmd, int yukID, int num, double val)
{
	super();

	setCommand(cmd);
	setYukonID(yukID);
	setNumber(num);
	setValue(val);
}

/**
 * LMCommand constructor comment.
 */
public LMCommand( int cmd, int yukID, int num, double val, int count_, int auxid_ )
{
	this( cmd, yukID, num, val );

	setCount( count_ );
	setAuxid( auxid_ );
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
 * Creation date: (4/3/2001 2:30:42 PM)
 * @return int
 */
public int getNumber() {
	return number;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 2:30:42 PM)
 * @return double
 */
public double getValue() {
	return value;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 10:47:14 AM)
 * @return int
 */
public int getYukonID() {
	return yukonID;
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
 * Creation date: (4/3/2001 2:30:42 PM)
 * @param newNumber int
 */
public void setNumber(int newNumber) {
	number = newNumber;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 2:30:42 PM)
 * @param newValue double
 */
public void setValue(double newValue) {
	value = newValue;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 10:47:14 AM)
 * @param newYukonID int
 */
public void setYukonID(int newYukonID) {
	yukonID = newYukonID;
}
	/**
	 * Returns the auxid.
	 * @return int
	 */
	public int getAuxid()
	{
		return auxid;
	}

	/**
	 * Returns the count.
	 * @return int
	 */
	public int getCount()
	{
		return count;
	}

	/**
	 * Sets the auxid.
	 * @param auxid The auxid to set
	 */
	public void setAuxid(int auxid)
	{
		this.auxid = auxid;
	}

	/**
	 * Sets the count.
	 * @param count The count to set
	 */
	public void setCount(int count)
	{
		this.count = count;
	}

}
