package com.cannontech.message.notif;

import java.util.Date;

import com.cannontech.message.util.Message;

public class ProgramActionMsg extends Message {

    public Integer programId;
    public String eventDisplayName;
    public String action;
    public Date startTime;
    public Date stopTime;
    public Date notificationTime;
    public int[] customerIds;

    public ProgramActionMsg() {
        super();
    }

}
