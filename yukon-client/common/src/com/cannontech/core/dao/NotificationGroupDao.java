package com.cannontech.core.dao;

import java.util.Set;

import com.cannontech.database.data.lite.LiteNotificationGroup;

public interface NotificationGroupDao {
    String[] getNotifEmailsByLiteGroup(LiteNotificationGroup notificationGroup);

    LiteNotificationGroup getLiteNotificationGroup(int groupId);

    Set<LiteNotificationGroup> getAllNotificationGroups();
}
