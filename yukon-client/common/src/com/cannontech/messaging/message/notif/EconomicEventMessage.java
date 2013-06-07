package com.cannontech.messaging.message.notif;

import com.cannontech.enums.EconomicEventAction;
import com.cannontech.messaging.message.BaseMessage;

public class EconomicEventMessage extends BaseMessage {

    private int economicEventId;
    private int revisionNumber;
    private EconomicEventAction action;

    public int getEconomicEventId() {
        return economicEventId;
    }

    public void setEconomicEventId(int economicEventId) {
        this.economicEventId = economicEventId;
    }

    public int getRevisionNumber() {
        return revisionNumber;
    }

    public void setRevisionNumber(int revisionNumber) {
        this.revisionNumber = revisionNumber;
    }

    public EconomicEventAction getAction() {
        return action;
    }

    public void setAction(EconomicEventAction action) {
        this.action = action;
    }

    @Override
    public String toString() {
        return String.format("EconomicEventMsg [economicEventId=%s, revisionNumber=%s, action=%s, parent=%s]",
                             economicEventId, revisionNumber, action, super.toString());
    }
}
