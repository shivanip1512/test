package com.cannontech.notif.outputs;

import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.data.notification.NotifType;

/**
 * Handles all outgoing email messages for the NotificationServer.
 */
public class StandardEmailHandler extends GenericEmailHandler {

    static public final NotificationTypeChecker checker = new NotificationTypeChecker() {
        public boolean validNotifcationType(int notificationCategoryId) {
            return DaoFactory.getYukonListDao().isEmail(notificationCategoryId);
        };
    };

    public StandardEmailHandler() {
        super("email");
    }

    public NotificationTypeChecker getTypeChecker() {
        return checker;
    }

    public NotifType getNotificationMethod() {
        return NotifType.EMAIL;
    }

}
