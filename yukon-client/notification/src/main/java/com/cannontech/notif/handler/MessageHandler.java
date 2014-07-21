package com.cannontech.notif.handler;

import com.cannontech.message.util.Message;
import com.cannontech.notif.server.NotifServerConnection;

public interface MessageHandler<T extends Message> {

    /**
     * Overriding functions should process the Message, produce a Notification
     * object and a LiteNotificationGroup object, and call handleNotification().
     * 
     * @param connection 
     * @param message
     */
    public void handleMessage(NotifServerConnection connection, Message message);

    public Class<T> getSupportedMessageType();
}
