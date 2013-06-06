package com.cannontech.notif.outputs;

import com.cannontech.common.util.NotificationTypeChecker;
import com.cannontech.core.dao.YukonListDao;
import com.cannontech.database.data.notification.NotifType;
import com.cannontech.spring.YukonSpringHook;

public class SmsEmailHandler extends GenericEmailHandler {
    
    static public final NotificationTypeChecker checker = new NotificationTypeChecker() {
        public boolean validNotifcationType(int notificationCategoryId) {
            return YukonSpringHook.getBean(YukonListDao.class).isShortEmail(notificationCategoryId);
        };
    };

    public NotificationTypeChecker getTypeChecker() {
        return checker;
    }

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
