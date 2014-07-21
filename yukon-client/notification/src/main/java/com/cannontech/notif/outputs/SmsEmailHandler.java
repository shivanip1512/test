package com.cannontech.notif.outputs;

import com.cannontech.common.model.ContactNotificationType;
import com.cannontech.common.util.NotificationTypeChecker;
import com.cannontech.database.data.notification.NotifType;

public class SmsEmailHandler extends GenericEmailHandler {
    
    static public final NotificationTypeChecker checker = new NotificationTypeChecker() {
        @Override
        public boolean validNotifcationType(ContactNotificationType notificationType) {
            return notificationType.isShortEmailType();
        };
    };

    @Override
    public NotificationTypeChecker getTypeChecker() {
        return checker;
    }

    @Override
    public NotifType getNotificationMethod() {
        return NotifType.SMS;
    }

    @Override
    public NotifType getType() {
        return NotifType.SMS;
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
