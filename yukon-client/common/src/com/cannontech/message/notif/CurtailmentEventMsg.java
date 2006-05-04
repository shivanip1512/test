package com.cannontech.message.notif;

import com.cannontech.message.util.Message;

public class CurtailmentEventMsg extends Message {
    public static final int STARTING = 0;
    public static final int ADJUSTING = 1;
    public static final int CANCELLING = 2;
    
    public int curtailmentEventId;
    public int action;
    
}
