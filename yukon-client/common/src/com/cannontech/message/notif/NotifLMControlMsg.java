package com.cannontech.message.notif;

import java.util.Date;

import com.cannontech.message.util.Message;

public class NotifLMControlMsg extends Message {
	
	// types of control notifications 
	public static final int STARTING_CONTROL_NOTIFICATION = 1;
	public static final int STARTING_NEVER_STOP_CONTROL_NOTIFICATION = 2;	
	public static final int ADJUSTING_CONTROL_NOTIFICATION = 3;
	public static final int FINISHING_CONTROL_NOTIFICATION = 4;
	
	public int[] notifGroupIds = new int[0];
	public int notifType;
    public int programId;
    public Date startTime;
    public Date stopTime;
}
