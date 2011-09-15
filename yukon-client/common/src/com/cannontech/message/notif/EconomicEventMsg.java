package com.cannontech.message.notif;

import com.cannontech.enums.EconomicEventAction;
import com.cannontech.message.util.Message;

public class EconomicEventMsg extends Message {
    
    public int economicEventId;
    public int revisionNumber;
    public EconomicEventAction action;

    @Override
    public String toString() {
        return String.format("EconomicEventMsg [economicEventId=%s, revisionNumber=%s, action=%s, parent=%s]",
                             economicEventId,
                             revisionNumber,
                             action,
                             super.toString());
    }
}
