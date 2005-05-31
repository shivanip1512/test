package com.cannontech.notif.message;

import java.util.Date;
import java.util.Vector;

import com.cannontech.message.util.Message;

public class NotifLMControlMsg extends Message {
	
	// types of control notifications 
	public static final int STARTING_CONTROL_NOTIFICATION = 1;
	public static final int ADJUSTING_CONTROL_NOTIFICATION = 2;
	public static final int FINISHING_CONTROL_NOTIFICATION = 3;
	
	// Vector<int> contains notification group ids
	public Vector notifGroupIds = new Vector(2);
	public int notifType;
    public int programId;
    public Date startTime;
    public Date stopTime;
}
