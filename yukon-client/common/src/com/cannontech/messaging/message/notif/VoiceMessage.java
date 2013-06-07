package com.cannontech.messaging.message.notif;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.messaging.message.BaseMessage;

/**
 * Use the base classes message string to give a description of the error.
 */
public class VoiceMessage extends BaseMessage {

    private int notifProgramId = CtiUtilities.NONE_ZERO_ID;

    public int getNotifProgramId() {
        return notifProgramId;
    }

    public void setNotifProgramId(int i) {
        notifProgramId = i;
    }
}
