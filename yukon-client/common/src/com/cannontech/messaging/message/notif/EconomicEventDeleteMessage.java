package com.cannontech.messaging.message.notif;

import com.cannontech.messaging.message.BaseMessage;

public class EconomicEventDeleteMessage extends BaseMessage {

    private int economicEventId;
    private boolean deleteStart = true;
    private boolean deleteStop = true;

    public int getEconomicEventId() {
        return economicEventId;
    }

    public void setEconomicEventId(int economicEventId) {
        this.economicEventId = economicEventId;
    }

    public boolean isDeleteStart() {
        return deleteStart;
    }

    public void setDeleteStart(boolean deleteStart) {
        this.deleteStart = deleteStart;
    }

    public boolean isDeleteStop() {
        return deleteStop;
    }

    public void setDeleteStop(boolean deleteStop) {
        this.deleteStop = deleteStop;
    }

    @Override
    public String toString() {
        return String.format("EconomicEventDeleteMsg [economicEventId=%s, deleteStart=%s, deleteStop=%s, parent=%s]",
                             economicEventId, deleteStart, deleteStop, super.toString());
    }
}
