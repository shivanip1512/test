package com.cannontech.message.notif;

import com.cannontech.message.util.Message;

public class NotifCompletedMsg extends Message {
    public String token = "";
    public NotifCallEvent status = NotifCallEvent.UNCONFIRMED;
}
