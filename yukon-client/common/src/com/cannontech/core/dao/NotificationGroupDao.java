package com.cannontech.core.dao;

import com.cannontech.database.data.lite.LiteNotificationGroup;

public interface NotificationGroupDao {

    public String[] getNotifEmailsByLiteGroup(LiteNotificationGroup lGrp_);

    public LiteNotificationGroup getLiteNotificationGroup(int groupID_);

}