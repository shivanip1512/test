package com.cannontech.message.notif;

import java.util.Arrays;
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

    @Override
    public String toString() {
        return String
            .format("ProgramActionMsg [programId=%s, eventDisplayName=%s, action=%s, startTime=%s, stopTime=%s, notificationTime=%s, customerIds=%s, parent=%s]",
                    programId,
                    eventDisplayName,
                    action,
                    startTime,
                    stopTime,
                    notificationTime,
                    Arrays.toString(customerIds),
                    super.toString());
    }
}
