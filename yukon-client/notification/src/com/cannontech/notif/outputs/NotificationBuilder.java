package com.cannontech.notif.outputs;

import com.cannontech.database.data.notification.NotifType;


public interface NotificationBuilder {
    public Notification buildNotification(Contactable contact);
    public void notificationComplete(Contactable contact, NotifType notifType, boolean success);

}
