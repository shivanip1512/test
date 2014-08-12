package com.cannontech.common.util.jms;

import java.io.Serializable;

public interface RequestReplyTemplate<R extends Serializable> {
    public <Q extends Serializable> void send(Q requestPayload, JmsReplyHandler<R> callback);
}