package com.cannontech.cbc.exceptions;

public class CBCExceptionMessages {
    
    
    public static final String[] MSG_MULTIPLE_DEVICES_ON_PORT = {"ERROR: Master/Slave addressing already in use in device: ", 
                                                            ". Please edit the addressing or choose a different port."};

    public static final String MSG_SAME_MASTER_SLAVE_COMBINATION = "WARNING: Master/Slave addressing already in use in device: ";

	public static final String MSG_DUPLICATE_SERIAL_NUMBER = "The following devices have the same serial number: ";

	public static final String MSG_ALTSUB_NEEDS_SWITCH_PT = "ERROR: alternative sub bus needs to have a switch point";

	public static final String DB_UPDATE_SUCCESS = "Database update was SUCCESSFUL";

	public static final String MSG_NONAME_PAO = "Provide a name for the object you would like to create"; 

    
	public CBCExceptionMessages() {
        super();
    }

}
