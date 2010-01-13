package com.cannontech.yukon.cbc;

/**
 * CBCCommand objects are sent to the CBC server to request that an operation
 * be done on the given ID.  Clients only send CBCCommands
 * and the server only receives them.
 */

public class CapControlCommand extends com.cannontech.yukon.cbc.CapControlMessage 
{
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

	public static final int CONFIRM_FEEDER = 15;
	public static final int RESET_ALL_OPCOUNTS = 16;

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
    
    public static final int CMD_MANUAL_ENTRY = 30;
    public static final int CMD_BANK_TEMP_MOVE  = 31;
    
    public static final int SEND_ALL_ENABLE_OVUV = 31;
    public static final int SEND_ALL_DISABLE_OVUV = 32;
    public static final int SEND_ALL_SCAN_2WAY = 33;
    public static final int SEND_TIMESYNC = 34;
    public static final int OPERATIONAL_STATECHANGE = 35;
    public static final int SEND_ALL_ENABLE_TEMPCONTROL = 40;
    public static final int SEND_ALL_DISABLE_TEMPCONTROL = 41;
    public static final int SEND_ALL_ENABLE_VARCONTROL = 42;
    public static final int SEND_ALL_DISABLE_VARCONTROL = 43;
    public static final int SEND_ALL_ENABLE_TIMECONTROL = 44;
    public static final int SEND_ALL_DISABLE_TIMECONTROL = 45;
    
    public static final int BANK_ENABLE_TEMPCONTROL = 46;
    public static final int BANK_DISABLE_TEMPCONTROL = 47;
    public static final int BANK_ENABLE_VARCONTROL = 48;
    public static final int BANK_DISABLE_VARCONTROL = 49;
    public static final int BANK_ENABLE_TIMECONTROL = 50;
    public static final int BANK_DISABLE_TIMECONTROL = 51;
    
    public static final int SYNC_CBC_CAPBANK_STATE = 52;
    public static final int SYNC_ALL_CAPBANK_STATES = 53;
    
    public static final int VERIFY_OFFSET = 60;
    public static final int CMD_ALL_BANKS = VERIFY_OFFSET + 0;
    public static final int CMD_FQ_BANKS = VERIFY_OFFSET + 1;
    public static final int CMD_FAILED_BANKS = VERIFY_OFFSET + 2;
    public static final int CMD_QUESTIONABLE_BANKS = VERIFY_OFFSET + 3;
    public static final int CMD_DISABLE_VERIFY = VERIFY_OFFSET + 4;
    public static final int CMD_STANDALONE_VERIFY = VERIFY_OFFSET + 6;
    public static final int CONFIRM_SUBSTATION = VERIFY_OFFSET + 7;
    
    public static final int LTC_SCAN_INTEGRITY = 70;
    public static final int LTC_REMOTE_ENABLE = 71;
    public static final int LTC_REMOTE_DISABLE = 72;
    public static final int LTC_TAP_POSITION_RAISE = 73;
    public static final int LTC_TAP_POSITION_LOWER = 74;
    
	public static final String[] COMMAND_STRINGS = {
		"Enable Substation Bus",
		"Disable Substation Bus",
		"Enable Feeder",
		"Disable Feeder",
		
		"Enable Cap Bank",
		"Disable Cap Bank",  //5		
		"Open Cap Bank",
		"Close Cap Bank",
		"Confirm Open",
		"Confirm Close",
		"Request All Substation Buses",
		"Return Bank to Feeder",   //11
		"Update Op Count",
		"",
		"",
		"Confirm Feeder",  //15
		"Unwaive Feeder",
		"Enable OV/UV",
		"Disable OV/UV",
		"Delete Item", //19
		"Confirm Substation Bus",
        "Confirm Area", //21
        "Enable", //22
        "Disable", //23
        "Scan 2Way Device",//24
        "Enable System",
        "Disable System",
        "Flip 7010 Cap Bank",
		"System Status",
		"Send All Open", //29
		"Send All Close", //30
		"Send All Enable OVUV", //31
		"Send All Disable OVUV",
		"Scan All Scan 2Way CBCs",
		"Send TimeSync",
		"Change Operational State",
		"",
		"",
		"",
		"",
		"Enable Temp Control", //40
        "Disable Temp Control",
        "Enable Var Control",
        "Disable Var Control",
        "Enable Time Control",
        "Disable Time Control", //45
        "Enable Temp Control", 
        "Disable Temp Control",
        "Enable Var Control",
        "Disable Var Control",
        "Enable Time Control", //50
        "Disable Time Control",
        "Sync CapBank State",
        "Sync Mismatched CapBank State",
        "",
        "",
        "",
        "",
        "",
        "",
        "Verify All Banks", //60
        "Verify Failed And Questionable Banks",
        "Verify Failed Banks",
        "Verify Questionable Banks", //63
        "Verify Disabled Banks", //64
        "",
        "VerifyStandaloneBanks",
        "Confirm Substation",
        "",
        "",
        "Scan Ltc Integrity", //70
        "Enable Remote Control",
        "Disable Remote Control",
        "Raise Tap Position",
        "Lower Tap Position",
    };

	private int command;
	private int deviceID;
/**
 * ScheduleCommand constructor comment.
 */
public CapControlCommand() {
	super();
}
/**
 * ScheduleCommand constructor comment.
 */
public CapControlCommand(int command_, int deviceid_)
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
