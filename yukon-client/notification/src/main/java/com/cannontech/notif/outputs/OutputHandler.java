package com.cannontech.notif.outputs;

import com.cannontech.database.data.notification.NotifType;

public interface OutputHandler {

    public NotifType getType();

    public void handleNotification(NotificationBuilder notif, Contactable contact);
    
    public NotifType getNotificationMethod();
    
    public void startup();
    
    public void shutdown();
}
