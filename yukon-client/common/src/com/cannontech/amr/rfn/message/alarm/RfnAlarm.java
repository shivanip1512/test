package com.cannontech.amr.rfn.message.alarm;

import com.cannontech.amr.rfn.message.event.RfnEvent;

public class RfnAlarm extends RfnEvent {

    private static final long serialVersionUID = 2L;

    @Override
    public String toString() {
        return String.format("RfnAlarm [type=%s, rfnIdentifier=%s, timeStamp=%tc, alarmData=%s]",
                             type,
                             rfnIdentifier,
                             timeStamp,
                             eventData);
    }
    
}