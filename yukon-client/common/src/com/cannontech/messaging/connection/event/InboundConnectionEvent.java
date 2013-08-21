package com.cannontech.messaging.connection.event;

import com.cannontech.event.EventBase;
import com.cannontech.messaging.connection.Connection;
import com.cannontech.messaging.connection.ListenerConnection;

public class InboundConnectionEvent extends EventBase<ListenerConnection, Connection, InboundConnectionEventHandler> {

    @Override
    protected void notifyHandler(ListenerConnection eventSrc, Connection newServerConnection,
                                 InboundConnectionEventHandler handler) {
        handler.onInboundConnectionEvent(eventSrc, newServerConnection);
    }
}
