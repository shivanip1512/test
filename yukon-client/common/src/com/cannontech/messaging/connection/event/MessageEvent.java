package com.cannontech.messaging.connection.event;

import com.cannontech.event.EventBase;
import com.cannontech.messaging.connection.Connection;
import com.cannontech.messaging.message.BaseMessage;

public class MessageEvent extends EventBase<Connection, BaseMessage, MessageEventHandler> {

    @Override
    protected void notifyHandler(Connection eventSrc, BaseMessage eventArg, MessageEventHandler handler) {
        handler.onMessageEvent(eventSrc, eventArg);
    }
}
