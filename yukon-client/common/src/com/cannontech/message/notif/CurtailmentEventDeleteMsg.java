package com.cannontech.message.notif;

import com.cannontech.message.util.Message;

public class CurtailmentEventDeleteMsg extends Message {
    public int curtailmentEventId;
    public boolean deleteStart;
    public boolean deleteStop;

}
