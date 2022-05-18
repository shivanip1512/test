package com.cannontech.web.api.notificationGroup.service;

import java.util.List;

import com.cannontech.web.notificationGroup.NotificationGroup;

public interface NotificationGroupService {

    /**
     * Create a notification group
     */
    NotificationGroup create(NotificationGroup notificationGroup);

    /**
     * Delete a notification group
     */
    int delete(int id);

    /**
     * Retrieve all notification groups
     */
    List<NotificationGroup> retrieveAll();

}
