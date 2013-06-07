package com.cannontech.messaging.message.notif;

import com.cannontech.messaging.message.BaseMessage;

public class VoiceCompletedMessage extends BaseMessage {

    private String callToken = "";
    private CallStatus callStatus = CallStatus.UNCONFIRMED;

    public String getCallToken() {
        return callToken;
    }

    public void setCallToken(String callToken) {
        this.callToken = callToken;
    }

    public CallStatus getCallStatus() {
        return callStatus;
    }

    public void setCallStatus(CallStatus callStatus) {
        this.callStatus = callStatus;
    }

    @Override
    public String toString() {
        return String.format("VoiceCompletedMessage [callToken=%s, callStatus=%s, parent=%s]", callToken, callStatus,
                             super.toString());
    }
}
