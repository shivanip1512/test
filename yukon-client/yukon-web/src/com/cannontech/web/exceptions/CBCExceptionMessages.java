package com.cannontech.web.exceptions;

public class CBCExceptionMessages {
    
    
    public static final String[] MSG_MULTIPLE_DEVICES_ON_PORT = {"ERROR: Master/Slave addressing already in use in device: ", 
                                                            ". Please edit the addressing or choose a different port."};

    public static final String MSG_SAME_MASTER_SLAVE_COMBINATION = "WARNING: Master/Slave addressing already in use in device: "; 

    public CBCExceptionMessages() {
        super();
    }

}
