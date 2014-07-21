package com.cannontech.notif.outputs;

import com.cannontech.database.data.notification.NotifType;

public class SingleNotifContactable extends Contactable {

    private final NotifType _notifMethod;

    public SingleNotifContactable(ContactableBase base, NotifType notifMethod) {
        super(base);
        _notifMethod = notifMethod;
    }
    
    @Override
    public boolean supportsNotificationMethod(NotifType notificationMethod) {
        return _notifMethod.equals(notificationMethod);
    }

}
