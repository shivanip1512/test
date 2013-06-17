package com.cannontech.message.notif;

import com.cannontech.message.util.Message;

public class EconomicEventDeleteMsg extends Message {
    public int economicEventId;
    public boolean deleteStart = true;
    public boolean deleteStop = true;

    @Override
    public String toString() {
        return String
            .format("EconomicEventDeleteMsg [economicEventId=%s, deleteStart=%s, deleteStop=%s, parent=%s]",
                    economicEventId,
                    deleteStart,
                    deleteStop,
                    super.toString());
    }
}
