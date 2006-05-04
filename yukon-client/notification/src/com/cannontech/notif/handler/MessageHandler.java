package com.cannontech.notif.handler;

import com.cannontech.message.util.Message;
import com.cannontech.notif.server.NotifServerConnection;

public abstract class MessageHandler {

    /**
     * Overriding functions should process the Message, produce a Notification
     * object and a LiteNotificationGroup object, and call handleNotification().
     * @param connection 
     * @param msg_
     * @return true if processing should stop
     */
    public abstract boolean handleMessage(NotifServerConnection connection, Message msg_);

}
