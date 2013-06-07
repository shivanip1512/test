package com.cannontech.messaging.message.notif;

import com.cannontech.messaging.message.BaseMessage;

public class CurtailmentEventDeleteMessage extends BaseMessage {

    private int curtailmentEventId;
    private boolean deleteStart;
    private boolean deleteStop;

    public int getCurtailmentEventId() {
        return curtailmentEventId;
    }

    public void setCurtailmentEventId(int curtailmentEventId) {
        this.curtailmentEventId = curtailmentEventId;
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
        return String
            .format("CurtailmentEventDeleteMsg [curtailmentEventId=%s, deleteStart=%s, deleteStop=%s, parent=%s]",
                    curtailmentEventId, deleteStart, deleteStop, super.toString());
    }
}
