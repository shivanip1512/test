package com.cannontech.notif.handler;

import com.cannontech.database.data.lite.LiteNotificationGroup;
import com.cannontech.message.util.Message;
import com.cannontech.notif.outputs.Notification;
import com.cannontech.notif.outputs.OutputHandlerHelper;

public abstract class NotifHandler {
    private OutputHandlerHelper _helper;

    public NotifHandler(OutputHandlerHelper helper) {
        _helper = helper;
    }

    public abstract boolean canHandle(Message msg);

    /**
     * Overriding functions should process the Message, produce a Notification
     * object and a LiteNotificationGroup object, and call handleNotification().
     * @param msg_
     */
    public abstract void handleMessage(Message msg_);

    protected void outputNotification(Notification notif,
            LiteNotificationGroup lng) {
        _helper.handleNotification(notif, lng);
    }

}
