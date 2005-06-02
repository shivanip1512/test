package com.cannontech.notif.handler;

import com.cannontech.database.data.lite.LiteNotificationGroup;
import com.cannontech.notif.outputs.NotificationBuilder;
import com.cannontech.notif.outputs.OutputHandlerHelper;


public abstract class NotifHandler extends MessageHandler {
    private OutputHandlerHelper _helper;

    public NotifHandler(OutputHandlerHelper helper) {
        _helper = helper;
    }

    protected void outputNotification(NotificationBuilder notif,
            LiteNotificationGroup lng) {
        _helper.handleNotification(notif, lng);
    }

}
