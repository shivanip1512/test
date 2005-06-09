package com.cannontech.notif.handler;

import com.cannontech.message.util.Message;
import com.cannontech.notif.server.NotifServerConnection;

public abstract class MessageHandler {

    public abstract boolean canHandle(Message msg);

    /**
     * Overriding functions should process the Message, produce a Notification
     * object and a LiteNotificationGroup object, and call handleNotification().
     * @param connection 
     * @param msg_
     */
    public abstract void handleMessage(NotifServerConnection connection, Message msg_);

}
