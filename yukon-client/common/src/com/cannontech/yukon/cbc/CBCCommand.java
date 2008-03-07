package com.cannontech.yukon.cbc;

/**
 * CBCCommand objects are sent to the CBC server to request that an operation
 * be done on the given ID.  Clients only send CBCCommands
 * and the server only receives them.
 */

public class CBCCommand extends com.cannontech.yukon.cbc.CBCMessage 
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
	public static final int REQUEST_ALL_AREAS = 10;
	public static final int RETURN_BANK_TO_FEEDER = 11;
	public static final int RESET_OPCOUNT = 12;

	public static final int WAIVE_SUB = 13;
	public static final int UNWAIVE_SUB = 14;
	public static final int WAIVE_FEEDER = 15;
	public static final int UNWAIVE_FEEDER = 16;

	public static final int BANK_ENABLE_OVUV = 17;
	public static final int BANK_DISABLE_OVUV = 18;
	public static final int DELETE_ITEM = 19;
	public static final int CONFIRM_SUB = 20;

    public static final int CONFIRM_AREA = 21;
    public static final int ENABLE_AREA = 22;
    public static final int DISABLE_AREA = 23;
    public static final int SCAN_2WAY_DEV = 24;
    public static final int ENABLE_SYSTEM = 25;
    public static final int DISABLE_SYSTEM = 26;
    public static final int FLIP_7010_CAPBANK = 27;
    public static final int SYSTEM_STATUS = 28;
    public static final int SEND_ALL_OPEN = 29;
    public static final int SEND_ALL_CLOSE = 30;
    public static final int SEND_ALL_ENABLE_OVUV = 31;
    public static final int SEND_ALL_DISABLE_OVUV = 32;
    public static final int SEND_ALL_SCANN_2WAY = 33;
    public static final int SEND_TIMESYNC = 34;
    public static final int OPERATIONAL_STATECHANGE = 35;
    
	//server does not use these commands
	public static final int CMD_MANUAL_ENTRY = 30;
	public static final int CMD_BANK_TEMP_MOVE	= 31;
	public static final int VERIFY_OFFSET = 40;
    
	public static final int CMD_ALL_BANKS = VERIFY_OFFSET + 0;
	public static final int CMD_FQ_BANKS = VERIFY_OFFSET + 1;
	public static final int CMD_FAILED_BANKS = VERIFY_OFFSET + 2;
	public static final int CMD_QUESTIONABLE_BANKS = VERIFY_OFFSET + 3;
	public static final int CMD_DISABLE_VERIFY = VERIFY_OFFSET + 4;
    public static final int CMD_STANDALONE_VERIFY = VERIFY_OFFSET + 6;
    
    public static final int CONFIRM_SUBSTATION = VERIFY_OFFSET + 7;

	public static final String[] COMMAND_STRINGS = {
		"ENABLE SUBSTATION BUS",
		"DISABLE SUBSTATION BUS",
		"ENABLE FEEDER",
		"DISABLE FEEDER",
		
		"ENABLE CAPBANK",
		"DISABLE CAPBANK",  //5		
		"OPEN CAPBANK",
		"CLOSE CAPBANK",
		"CONFIRM OPEN",
		"CONFIRM CLOSE",
		"REQUEST ALL SUBSTATION BUSES",
		"RETURN BANK TO FEEDER",   //11
		"UPDATE OPCOUNT",
		"WAIVE SUB",
		"UNWAIVE SUB",
		"WAIVE FEEDER",  //15
		"UNWAIVE FEEDER",
		"ENABLE OV/UV",
		"DISABLE OV/UV",
		"DELETE ITEM", //19
		"CONFIRM SUB",
        "CONFIRM AREA", //21
        "ENABLE AREA", //22
        "DISABLE AREA", //23
        "SCAN 2WAY DEV",//24
        "ENABLE SYTEM",
        "DISABLE SYSTEM",
        "FLIP 7010 CAPBANK",
		"","",


		//below are not used by the server
		"Manual Entry", //30
		"Bank Temp Move", //31
		"",
		"Scan All 2Way CBCs",
		"",
		"",
		"",
		"",
		"",
		"",
		"AllBanks", //40 
		"FailedAndQuestionableBanks",
		"FailedBanks",
		"QuestionableBanks", //43
		"DisableVerify", //44
        "",
        "VerifyStandaloneBanks",
        "CONFIRM SUBSTATIONS"
        };
	


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

public boolean isSystemDisabled () {
    return (getCommand() == SYSTEM_STATUS && getDeviceID() == 0);
}

}
