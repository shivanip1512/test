package com.cannontech.message.notif;

import com.cannontech.message.util.Message;

public class CurtailmentEventDeleteMsg extends Message {
    public int curtailmentEventId;
    public boolean deleteStart;
    public boolean deleteStop;

    @Override
    public String toString() {
        return String
            .format("CurtailmentEventDeleteMsg [curtailmentEventId=%s, deleteStart=%s, deleteStop=%s, parent=%s]",
                    curtailmentEventId,
                    deleteStart,
                    deleteStop,
                    super.toString());
    }
}
