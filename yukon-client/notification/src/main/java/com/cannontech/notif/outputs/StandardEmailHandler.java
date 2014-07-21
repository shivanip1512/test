package com.cannontech.notif.outputs;

import com.cannontech.common.model.ContactNotificationType;
import com.cannontech.common.util.NotificationTypeChecker;
import com.cannontech.database.data.notification.NotifType;

/**
 * Handles all outgoing email messages for the NotificationServer.
 */
public class StandardEmailHandler extends GenericEmailHandler {

    //TEM consider using NotifTypeCheckerFactory here...
    static public final NotificationTypeChecker checker = new NotificationTypeChecker() {
        @Override
        public boolean validNotifcationType(ContactNotificationType notificationType) {
            return notificationType.isEmailType();
        };
    };

    @Override
    public NotificationTypeChecker getTypeChecker() {
        return checker;
    }

    @Override
    public NotifType getNotificationMethod() {
        return NotifType.EMAIL;
    }

    @Override
    public NotifType getType() {
        return NotifType.EMAIL;
    }

    @Override
    public void startup() {
        //No op
    }

    @Override
    public void shutdown() {
        //No op
    }

}
