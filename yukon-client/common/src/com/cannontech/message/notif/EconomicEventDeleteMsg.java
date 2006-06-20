package com.cannontech.message.notif;

import com.cannontech.message.util.Message;

public class EconomicEventDeleteMsg extends Message {
    public int economicEventId;
    public boolean deleteStart = true;
    public boolean deleteStop = true;

}
