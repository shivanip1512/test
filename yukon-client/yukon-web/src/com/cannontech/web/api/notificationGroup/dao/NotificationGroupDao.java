package com.cannontech.web.api.notificationGroup.dao;

import java.util.List;

import com.cannontech.common.model.Direction;
import com.cannontech.web.notificationGroup.NotificationGroup;

public interface NotificationGroupDao {
    List<NotificationGroup> getAllNotificationGroups(String sortBy, Direction direction);

}
