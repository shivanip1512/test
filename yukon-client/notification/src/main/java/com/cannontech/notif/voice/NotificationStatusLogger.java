package com.cannontech.notif.voice;

import com.cannontech.database.data.lite.LiteContactNotification;

public interface NotificationStatusLogger {
    public void logIndividualNotification(LiteContactNotification destination, boolean success);
}
