package com.cannontech.notif.handler;

import com.cannontech.messaging.message.BaseMessage;
import com.cannontech.notif.server.NotifServerConnection;

public interface MessageHandler<T extends BaseMessage> {

    /**
     * Overriding functions should process the Message, produce a Notification
     * object and a LiteNotificationGroup object, and call handleNotification().
     * 
     * @param connection 
     * @param message
     */
    public void handleMessage(NotifServerConnection connection, BaseMessage message);

    public Class<T> getSupportedMessageType();
}
