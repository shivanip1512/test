package com.cannontech.message.notif;

import java.util.Date;

import com.cannontech.message.util.Message;


public class NotifAlarmMsg extends Message {
	
	public int[] notifGroupIds = new int[0];
    public int alarmCategoryId;
    public int pointId;
    public int condition;
    public double value;
    public Date alarmTimestamp;
    public boolean acknowledged;
    public boolean abnormal;

}
