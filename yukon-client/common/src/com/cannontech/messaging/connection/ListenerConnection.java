package com.cannontech.messaging.connection;

import com.cannontech.event.Event;
import com.cannontech.messaging.connection.event.InboundConnectionEventHandler;

/**
 * This interface represents a Connection that has the ability to accept incoming connection request (as a server
 * would). Each incoming request fires the exposed
 * {@link com.cannontech.messaging.connection.event#InboundConnectionEvent InboundConnectionEvent}
 */
public interface ListenerConnection extends Connection {
    Event<InboundConnectionEventHandler> getInboundConnectionEvent();
}
