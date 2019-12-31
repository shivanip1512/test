package com.cannontech.stars.dr.jms.message;

import java.io.Serializable;

public abstract class DrJmsMessage implements Serializable {

    private static final long serialVersionUID = 1L;
    private DrJmsMessageType messageType;

    public DrJmsMessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(DrJmsMessageType messageType) {
        this.messageType = messageType;
    }

}
