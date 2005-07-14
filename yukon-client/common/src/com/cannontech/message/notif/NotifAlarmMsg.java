package com.cannontech.message.notif;

import com.cannontech.message.util.Message;


public class NotifAlarmMsg extends Message {
	
	public int[] notifGroupIds = new int[0];	
    public int pointId;
    public int condition;
    public double value;
    public boolean acknowledged;
    public boolean abnormal;

}
