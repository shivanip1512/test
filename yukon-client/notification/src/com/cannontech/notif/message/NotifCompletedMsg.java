package com.cannontech.notif.message;

import com.cannontech.message.util.Message;

public class NotifCompletedMsg extends Message {
    public String token;
    public boolean gotConfirmation;
}
