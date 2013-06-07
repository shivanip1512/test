package com.cannontech.messaging.message.notif;

import com.cannontech.enums.CurtailmentEventAction;
import com.cannontech.messaging.message.BaseMessage;

public class CurtailmentEventMessage extends BaseMessage {

    private int curtailmentEventId;
    private CurtailmentEventAction action;

    public int getCurtailmentEventId() {
        return curtailmentEventId;
    }

    public void setCurtailmentEventId(int curtailmentEventId) {
        this.curtailmentEventId = curtailmentEventId;
    }

    public CurtailmentEventAction getAction() {
        return action;
    }

    public void setAction(CurtailmentEventAction action) {
        this.action = action;
    }

    @Override
    public String toString() {
        return String.format("CurtailmentEventMsg [curtailmentEventId=%s, action=%s, parent=%s]", curtailmentEventId,
                             action, super.toString());
    }
}
