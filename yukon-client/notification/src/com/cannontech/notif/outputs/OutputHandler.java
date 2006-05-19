package com.cannontech.notif.outputs;

import com.cannontech.database.data.notification.NotifType;



/**
 * 
 */
public abstract class OutputHandler {

    private String _type;

    OutputHandler(String type) {
        _type = type;
    }

    public abstract void handleNotification(NotificationBuilder notif,
            Contactable contact);

    public String getType() {
        return _type;
    }
    
    public void startup() {};
    public void shutdown() {}

    public abstract NotifType getNotificationMethod();
    
    

}
