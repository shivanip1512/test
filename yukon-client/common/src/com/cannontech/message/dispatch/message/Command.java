package com.cannontech.message.dispatch.message;

/**
 * This type was created in VisualAge.
 */
public class Command extends com.cannontech.message.util.Message 
{
	//Operations that Van Gogh recognizes
	public static final int NO_OP = 0;		//Same as LOOP_CLIENT
	public static final int SHUTDOWN = 10;  //Shuts down Van Gogh! careful
	public static final int CLIENT_APP_SHUTDOWN = 20;  //We are going away
	public static final int NEW_CLIENT = 30;	//Client App has been connected by Connection Handler
	public static final int LOOP_CLIENT = 40;  //Send a mesage back to client
	public static final int TRACE_ROUTE = 50; //Prints a blurb out in each place it is encountered.
	public static final int ARE_YOU_THERE = 60;
	public static final int ACKNOWLEGDE_ALARM = 70;
	public static final int CLEAR_ALARM = 80;
	public static final int TOKEN_GRANT = 90;
	public static final int REREGISTRATION_REQUEST = 100;	
	public static final int DEVICE_SCAN_FAILED = 110;
	public static final int CONTROL_REQUEST = 120;
	public static final int ABLEMENT_TOGGLE = 130;
	public static final int COMM_STATUS = 140; // Vector contains token, deviceid,status (communication result in porter, 0 = NORMAL).
	public static final int ALTERNATE_SCAN_RATE = 150;
	public static final int CONTROL_ABLEMENT = 160;
    public static final int POINT_TAG_ADJUST = 170;  //vector (token, pointID, tagsToSet[], tagsToReset[])
    public static final int PORTER_CONSOLE_INPUT = 180; //vector (token, operation)
	public static final int RESET_CNTRL_HOURS = 190; //Resets the Seasonal control history hours to zero
	
	
	private static final int LAST_COMMAND = 10000; //Make this big


	// ABLEMTENT_TOGGLE used variables
	public static final int ABLEMENT_DEVICE_IDTYPE = 0;
	public static final int ABLEMENT_POINT_IDTYPE = 1;
	public static final int ABLEMENT_DISABLE = 0;
	public static final int ABLEMENT_ENABLE = 1;

	// default clientRegistrationToken as of 1-16-2000( Will change when it will be used!! )
	public static final int DEFAULT_CLIENT_REGISTRATION_TOKEN = -1;
	public static final int ACK_ALL_TOKEN = -1;
	
	private int operation = 0;
	private java.lang.String opString = "";
	private java.util.Vector opArgList = new java.util.Vector();
/**
 * Command constructor comment.
 */
public Command() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (6/15/00 2:35:20 PM)
 * @return java.util.Vector
 */
public java.util.Vector getOpArgList() {
	return opArgList;
}
/**
 * This method was created in VisualAge.
 * @return int
 */
public int getOperation() {
	return this.operation;
}
/**
 * Insert the method's description here.
 * Creation date: (6/15/00 2:34:51 PM)
 * @return java.lang.String
 */
public java.lang.String getOpString() {
	return opString;
}
/**
 * Insert the method's description here.
 * Creation date: (6/15/00 2:35:20 PM)
 * @param newOpArgList java.util.Vector
 */
public void setOpArgList(java.util.Vector newOpArgList) {
	opArgList = newOpArgList;
}
/**
 * This method was created in VisualAge.
 * @param operation int
 */
public void setOperation(int operation) {

	if( operation >= NO_OP && operation <= LAST_COMMAND )
		this.operation = operation;
	else
		this.operation = NO_OP;
}
/**
 * Insert the method's description here.
 * Creation date: (6/15/00 2:34:51 PM)
 * @param newOpString java.lang.String
 */
public void setOpString(java.lang.String newOpString) {
	opString = newOpString;
}
}
