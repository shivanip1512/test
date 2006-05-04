package com.cannontech.notif.outputs;

public class SingleNotifContactable extends Contactable {

    private final int _notifMethodId;

    public SingleNotifContactable(ContactableBase base, int notifMethodId) {
        super(base);
        _notifMethodId = notifMethodId;
    }
    
    @Override
    public boolean supportsNotificationMethod(int notificationMethod) {
        return _notifMethodId == notificationMethod;
    }

}
