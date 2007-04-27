package com.cannontech.notif.outputs;

import com.cannontech.database.data.lite.LiteContactNotification;
import com.cannontech.database.data.notification.NotifType;


public interface NotificationBuilder {
    public Notification buildNotification(Contactable contact);
    public void notificationComplete(Contactable contact, NotifType notifType, boolean success);
    public void logIndividualNotification(LiteContactNotification destination, Contactable contactable, 
            NotifType notifType, boolean success);

}
