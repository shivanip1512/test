package com.cannontech.messaging.message.loadcontrol;

import com.cannontech.messaging.message.BaseMessage;

public class LmMessage extends BaseMessage {

    private String message = new String();

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
