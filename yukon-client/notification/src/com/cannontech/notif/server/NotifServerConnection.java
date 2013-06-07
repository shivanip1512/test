package com.cannontech.notif.server;

import com.cannontech.messaging.connection.Connection;
import com.cannontech.messaging.util.ClientConnection;

public class NotifServerConnection extends ClientConnection {
    public NotifServerConnection(Connection connection, NotificationMessageHandler handler) {
        super(connection);
        addMessageListener(handler);
    }
}
