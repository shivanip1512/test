package com.cannontech.message.dispatch.message;

/**
 * This type was created in VisualAge.
 */
public class Command extends com.cannontech.message.util.Message 
{
	//Operations that Van Gogh recognizes
	public static final int NO_OP = 0;		//Same as LOOP_CLIENT
	public static final int SHUTDOWN = 1;  //Shuts down Van Gogh! careful
	public static final int CLIENT_APP_SHUTDOWN = 2;  //We are going away
	public static final int NEW_CLIENT = 3;	//Client App has been connected by Connection Handler
	public static final int LOOP_CLIENT = 4;  //Send a mesage back to client
	public static final int TRACE_ROUTE = 5; //Prints a blurb out in each place it is encountered.
	public static final int ARE_YOU_THERE = 6;
	public static final int ACKNOWLEGDE_ALARM = 7;
	public static final int CLEAR_ALARM = 8;
	public static final int TOKEN_GRANT = 9;
	public static final int REREGISTRATION_REQUEST = 10;	
	public static final int DEVICE_SCAN_FAILED = 11;
	public static final int CONTROL_REQUEST = 12;
	public static final int ABLEMENT_TOGGLE = 13;
	public static final int COMM_STATUS =14; // Vector contains token, deviceid,status (communication result in porter, 0 = NORMAL).
	public static final int ALTERNATE_SCAN_RATE = 15;
	public static final int CONTROL_ABLEMENT = 16;
	public static final int RESET_CNTRL_HOURS = 17; //Resets the Seasonal control history hours to zero
	
	
	public static final int LAST_COMMAND = 18; //Always increment this value when adding any new commands


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
