package com.cannontech.notif.server;

import com.cannontech.message.util.ClientConnection;
import com.cannontech.messaging.connection.Connection;

public class NotifServerConnection extends ClientConnection {
    public NotifServerConnection(Connection connection, NotificationMessageHandler handler) {
        super(connection);
        addMessageListener(handler);
    }
}
