package com.cannontech.notif.outputs;

import com.cannontech.common.util.NotificationTypeChecker;
import com.cannontech.core.dao.YukonListDao;
import com.cannontech.database.data.notification.NotifType;
import com.cannontech.spring.YukonSpringHook;

/**
 * Handles all outgoing email messages for the NotificationServer.
 */
public class StandardEmailHandler extends GenericEmailHandler {

    //TEM consider using NotifTypeCheckerFactory here...
    static public final NotificationTypeChecker checker = new NotificationTypeChecker() {
        public boolean validNotifcationType(int notificationCategoryId) {
            return YukonSpringHook.getBean(YukonListDao.class).isEmail(notificationCategoryId);
        };
    };

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
