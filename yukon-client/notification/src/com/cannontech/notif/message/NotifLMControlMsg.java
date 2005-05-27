package com.cannontech.notif.message;

import java.util.Date;

import com.cannontech.message.util.Message;

public class NotifLMControlMsg extends Message {
    public int programId;
    public Date startTime;
    public Date stopTime;
}
