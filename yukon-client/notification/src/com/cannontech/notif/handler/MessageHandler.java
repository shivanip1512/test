package com.cannontech.notif.handler;

import com.cannontech.message.util.Message;
import com.cannontech.notif.server.NotifServerConnection;

public interface MessageHandler {

    /**
     * Overriding functions should process the Message, produce a Notification
     * object and a LiteNotificationGroup object, and call handleNotification().
     * 
     * @param connection 
     * @param message
     */
    public void handleMessage(NotifServerConnection connection, Message message);

    public boolean supportsMessageType(Message message);
}
