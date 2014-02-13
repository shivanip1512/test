package com.cannontech.core.dao;

import java.util.List;
import java.util.Set;

import com.cannontech.database.data.lite.LiteNotificationGroup;

public interface NotificationGroupDao {
    List<String> getContactNoficationEmails(LiteNotificationGroup notificationGroup);

    LiteNotificationGroup getLiteNotificationGroup(int groupId);

    Set<LiteNotificationGroup> getAllNotificationGroups();
}
