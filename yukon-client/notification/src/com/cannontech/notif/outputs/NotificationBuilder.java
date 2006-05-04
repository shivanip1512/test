package com.cannontech.notif.outputs;


public interface NotificationBuilder {
    public Notification buildNotification(Contactable contact);
    public void notificationComplete(Contactable contact, int notifType, boolean success);

}
