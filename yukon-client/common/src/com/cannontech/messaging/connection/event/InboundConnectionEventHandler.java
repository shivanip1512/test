package com.cannontech.messaging.connection.event;

import com.cannontech.event.EventHandler;
import com.cannontech.messaging.connection.Connection;
import com.cannontech.messaging.connection.ListenerConnection;

public interface InboundConnectionEventHandler extends EventHandler {
    void onInboundConnectionEvent(ListenerConnection source, Connection newServerConnection);
}
