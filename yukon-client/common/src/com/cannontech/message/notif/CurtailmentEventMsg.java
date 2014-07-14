package com.cannontech.message.notif;

import com.cannontech.cc.service.CurtailmentEventAction;
import com.cannontech.message.util.Message;

public class CurtailmentEventMsg extends Message {
    public int curtailmentEventId;
    public CurtailmentEventAction action;

    @Override
    public String toString() {
        return String.format("CurtailmentEventMsg [curtailmentEventId=%s, action=%s, parent=%s]",
                             curtailmentEventId,
                             action,
                             super.toString());
    }
}
