package com.cannontech.core.dao;

import java.util.List;
import java.util.Set;

import com.cannontech.database.data.lite.LiteNotificationGroup;

public interface NotificationGroupDao {

    /**
     * Returns a list of email addresses to be sent to for notificationGroup.
     * Must be active (enabled) email type notifications.
     * This method should be deleted when YUK-13186 is fully corrected.
     * And callers should utilize the Notification Server messaging instead.
     * @deprecated
     */
    @Deprecated
    List<String> getContactNoficationEmails(LiteNotificationGroup notificationGroup);

    LiteNotificationGroup getLiteNotificationGroup(int groupId);

    Set<LiteNotificationGroup> getAllNotificationGroups();
}
