package com.cannontech.notif.handler;

import com.cannontech.message.util.Message;

public abstract class MessageHandler {

    public abstract boolean canHandle(Message msg);

    /**
     * Overriding functions should process the Message, produce a Notification
     * object and a LiteNotificationGroup object, and call handleNotification().
     * @param msg_
     */
    public abstract void handleMessage(Message msg_);

}
