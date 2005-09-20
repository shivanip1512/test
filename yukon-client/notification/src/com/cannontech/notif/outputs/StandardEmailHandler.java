package com.cannontech.notif.outputs;

import com.cannontech.database.cache.functions.YukonListFuncs;
import com.cannontech.database.data.notification.NotifMap;

/**
 * Handles all outgoing email messages for the NotificationServer.
 */
public class StandardEmailHandler extends GenericEmailHandler {

    static public final NotificationTypeChecker checker = new NotificationTypeChecker() {
        public boolean validNotifcationType(int notificationCategoryId) {
            return YukonListFuncs.isEmail(notificationCategoryId);
        };
    };

    public StandardEmailHandler() {
        super("email");
    }

    public NotificationTypeChecker getTypeChecker() {
        return checker;
    }

    public int getNotificationMethod() {
        return NotifMap.METHOD_EMAIL;
    }

}
