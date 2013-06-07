package com.cannontech.messaging.message.notif;

import com.cannontech.messaging.message.BaseMessage;

public class VoiceDataRequestMessage extends BaseMessage {

    private String callToken;

    public String getCallToken() {
        return callToken;
    }

    public void setCallToken(String callToken) {
        this.callToken = callToken;
    }
}
