package com.cannontech.broker.message.request;

import java.io.Serializable;

public class LoggingDbChangeRequest implements Serializable {

    private static final long serialVersionUID = 1L;
    private EventType type;

    public LoggingDbChangeRequest(EventType type) {
        super();
        this.type = type;
    }

    public EventType getType() {
        return type;
    }

    public void setType(EventType type) {
        this.type = type;
    }

}
