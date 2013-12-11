package com.cannontech.messaging.connection;

import java.net.URI;

import com.cannontech.event.Event;
import com.cannontech.messaging.connection.event.ConnectionEventHandler;
import com.cannontech.messaging.connection.event.MessageEventHandler;
import com.cannontech.message.util.Message;

public interface Connection {

    /**
     * Initializes and starts the connection asynchronously.
     */
    void start();

    /**
     * Close the connection a release all associated resources.
     */
    void close();

    /**
     * Send a message to this connection sending queue
     * @param message The message to send through this connection
     * @throws ConnectionException
     */
    void send(Message message) throws ConnectionException;

    /**
     * Retrieves the connection event of this connection. One should register to this event in order be notified about
     * ConnectionState change.
     * @return the connection event one should register to in order to be notified of connection state change.
     */
    Event<ConnectionEventHandler> getConnectionEvent();

    /**
     * Retrieves the message event of this connection. One should register to this event in order be notified about
     * received messages.
     * @return the message event one should register to in order to receive incoming messages.
     */
    Event<MessageEventHandler> getMessageEvent();

    String getName();

    void setName(String string);

    /**
     * Retrieves the connection current state
     * @return
     */
    ConnectionState getState();

    /**
     * Did the connection failed to connect after at least one attempt. The connection may still try to reconnect even if this method returns True.
     * @return True if the connection has fail to connect and is not currently connected, False otherwise.
     */
    boolean isConnectionFailed();

    /**
     * Retrieves the URI associated with this connection
     * @return the connection URI
     * @throws ConnectionException
     */
    URI getConnectionUri() throws ConnectionException;

    /**
     * Sets the ability of this connection to reconnect automatically after a connection has been lost.
     * @param autoReconnect
     */
    void setAutoReconnect(boolean autoReconnect);

    boolean isAutoReconnect();

    /**
     * Enum representing the state of this connection. Note that a once Closed, a connection can not be reopened.
     */
    enum ConnectionState {
        New,            // Connection has never been started
        Connecting,     // Establishing connection
        Connected,      // Connected (message can be sent)
        Disconnected,   // Disconnected but could still be reconnecting later if isAutoReconnect is true
        Closed,         // Connection is definitely closed
        Error           // Connection is definitely closed because of an internal error
    }
}
