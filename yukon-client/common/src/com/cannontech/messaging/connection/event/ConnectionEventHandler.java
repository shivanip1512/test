package com.cannontech.messaging.connection.event;

import com.cannontech.event.EventHandler;
import com.cannontech.messaging.connection.Connection;
import com.cannontech.messaging.connection.Connection.ConnectionState;

public interface ConnectionEventHandler extends EventHandler {
    void onConnectionEvent(Connection source, ConnectionState state);
}
