package com.cannontech.cbc.messages;

/**
 * ScheduleCommand objects are sent to the CBC server to request that an operation
 * be done on the given strategy.  Clients only send CBCCommands
 * and the server only receives them.
 */

public class CBCCommand extends com.cannontech.cbc.messages.CBCMessage 
{


	//The following are the different commands that
	//can be applied to strategies and map into the C++ side
	public static final int ENABLE_SUBBUS = 0;
	public static final int DISABLE_SUBBUS = 1;
	public static final int ENABLE_FEEDER = 2;
	public static final int DISABLE_FEEDER = 3;
	public static final int ENABLE_CAPBANK = 4;
	public static final int DISABLE_CAPBANK = 5;	
	public static final int OPEN_CAPBANK = 6;
	public static final int CLOSE_CAPBANK = 7;	
	public static final int CONFIRM_OPEN = 8;
	public static final int CONFIRM_CLOSE = 9;	
	public static final int REQUEST_ALL_SUBS = 10;
	public static final int RETURN_BANK_TO_FEEDER = 11;


	public static final String[] COMMAND_STRINGS =
	{
		"ENABLE SUBSTATION BUS",
		"DISABLE SUBSTATION BUS",
		"ENABLE FEEDER",
		"DISABLE FEEDER",
		
		"ENABLE CAPBANK",
		"DISABLE CAPBANK",		
		"OPEN CAPBANK",
		"CLOSE CAPBANK",
		"CONFIRM OPEN",
		"CONFIRM CLOSE",
		"REQUEST ALL SUBSTATION BUSES",
		"RETURN BANK TO FEEDER"
	};
	

	//server does not use these commands
	public static final int CMD_INVALID			= -1;
	public static final int CMD_MANUAL_ENTRY	= -2;


	private int command;
	private int deviceID;
/**
 * ScheduleCommand constructor comment.
 */
public CBCCommand() {
	super();
}
/**
 * ScheduleCommand constructor comment.
 */
public CBCCommand(int command_, int deviceid_)
{
	super();
	setCommand( command_ );
	setDeviceID( deviceid_ );
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
 * Creation date: (10/17/00 3:54:07 PM)
 * @return int
 */
public int getDeviceID() {
	return deviceID;
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
 * Creation date: (10/17/00 3:54:07 PM)
 * @param newDeviceID int
 */
public void setDeviceID(int newDeviceID) {
	deviceID = newDeviceID;
}
}
