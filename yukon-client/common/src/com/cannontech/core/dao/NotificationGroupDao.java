package com.cannontech.core.dao;

import java.util.Set;

import com.cannontech.database.data.lite.LiteNotificationGroup;

public interface NotificationGroupDao {

    public String[] getNotifEmailsByLiteGroup(LiteNotificationGroup lGrp_);

    public LiteNotificationGroup getLiteNotificationGroup(int groupID_);
    public Set<LiteNotificationGroup> getAllNotificationGroups();

}