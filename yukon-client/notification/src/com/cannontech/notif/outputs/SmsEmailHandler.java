package com.cannontech.notif.outputs;

import com.cannontech.database.cache.functions.YukonListFuncs;
import com.cannontech.database.data.notification.NotifType;

public class SmsEmailHandler extends GenericEmailHandler {

    public SmsEmailHandler() {
        super("sms");
    }
    
    static public final NotificationTypeChecker checker = new NotificationTypeChecker() {
        public boolean validNotifcationType(int notificationCategoryId) {
            return YukonListFuncs.isShortEmail(notificationCategoryId);
        };
    };

    public NotificationTypeChecker getTypeChecker() {
        return checker;
    }

    public NotifType getNotificationMethod() {
        return NotifType.SMS;
    }

}
