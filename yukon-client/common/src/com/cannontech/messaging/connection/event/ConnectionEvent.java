package com.cannontech.messaging.connection.event;

import com.cannontech.event.EventBase;
import com.cannontech.messaging.connection.Connection;
import com.cannontech.messaging.connection.Connection.ConnectionState;

public class ConnectionEvent extends EventBase<Connection, ConnectionState, ConnectionEventHandler> {

    @Override
    protected void notifyHandler(Connection connection, ConnectionState state, ConnectionEventHandler handler) {
        handler.onConnectionEvent(connection, state);
    }
}
