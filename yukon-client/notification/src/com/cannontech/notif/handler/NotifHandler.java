package com.cannontech.notif.handler;

import java.util.List;

import com.cannontech.database.data.lite.LiteNotificationGroup;
import com.cannontech.notif.outputs.*;


public abstract class NotifHandler extends MessageHandler {
    private OutputHandlerHelper _helper;

    public NotifHandler(OutputHandlerHelper helper) {
        _helper = helper;
    }

    protected void outputNotification(NotificationBuilder notif,
            LiteNotificationGroup lng) {
        List contactables = NotifMapContactable.getContactablesForGroup(lng);
        _helper.handleNotification(notif, contactables);
    }

}
