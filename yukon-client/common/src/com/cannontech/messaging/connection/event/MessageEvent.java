package com.cannontech.messaging.connection.event;

import com.cannontech.event.EventBase;
import com.cannontech.message.util.Message;
import com.cannontech.messaging.connection.Connection;

public class MessageEvent extends EventBase<Connection, Message, MessageEventHandler> {

    @Override
    protected void notifyHandler(Connection eventSrc, Message eventArg, MessageEventHandler handler) {
        handler.onMessageEvent(eventSrc, eventArg);
    }
}
