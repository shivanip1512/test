package com.cannontech.web.api.notificationGroup.service;

import com.cannontech.web.notificationGroup.NotificationGroup;

public interface NotificationGroupService {

    /**
     * Create a notification group
     */
    public NotificationGroup create(NotificationGroup notificationGroup);

    /**
     * Delete a notification group
     */
    int delete(int id);
}
