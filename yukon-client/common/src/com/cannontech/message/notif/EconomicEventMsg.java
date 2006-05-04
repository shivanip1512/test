package com.cannontech.message.notif;

import com.cannontech.enums.EconomicEventAction;
import com.cannontech.message.util.Message;

public class EconomicEventMsg extends Message {
    
    public int economicEventId;
    public int revisionNumber;
    public EconomicEventAction action;
    
}
