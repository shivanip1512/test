package com.cannontech.web.api.notificationGroup.service;

import com.cannontech.web.notificationGroup.NotificationGroup;

public interface NotificationGroupService {

    /**
     * Retrieve a notification group using id
     */
    NotificationGroup retrieve(int id);

    /**
     * Create a notification group
     */
    NotificationGroup create(NotificationGroup notificationGroup);

    /**
     * Delete a notification group
     */
    int delete(int id);
}
