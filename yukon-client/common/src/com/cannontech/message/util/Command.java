package com.cannontech.message.util;

import java.util.ArrayList;
import java.util.List;

public class Command extends Message {
    
    //Operations that Van Gogh recognizes
    public static final int NO_OP = 0;        //Same as LOOP_CLIENT
    public static final int SHUTDOWN = 10;  //Shuts down Van Gogh! careful
//    public static final int CLIENT_APP_SHUTDOWN = 20;  //We are going away
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
    public static final int POINT_DATA_REQUEST = 200; // List of point ids to be report if knowne
    public static final int ALARM_CATEGORY_REQUEST = 210; // List of category ids - point ids in these alarm categories will be reported
    public static final int ANALOG_OUTPUT_REQUEST = 220; // point id, value
    
    public static final int POINT_DATA_DEBUG = 260; // point id for Dispatch to report on
    
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
    private String opString = "";
    private List<Integer> opArgList = new ArrayList<Integer>();
    
    public List<Integer> getOpArgList() {
        return opArgList;
    }
    
    public void setOpArgList(List<Integer> opArgList) {
        this.opArgList = opArgList;
    }
    
    public int getOperation() {
        return this.operation;
    }
    
    public void setOperation(int operation) {
        
        if (operation >= NO_OP && operation <= LAST_COMMAND) {
            this.operation = operation;
        } else {
            this.operation = NO_OP;
        }
    }
    
    public String getOpString() {
        return opString;
    }
    
    public void setOpString(String opString) {
        this.opString = opString;
    }
    
    @Override
    public String toString() {
        return String.format("Command [operation=%s, opString=%s, opArgList=%s]", operation, opString, opArgList);
    }
    
}