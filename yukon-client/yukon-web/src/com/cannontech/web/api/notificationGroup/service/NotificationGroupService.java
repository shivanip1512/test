package com.cannontech.web.api.notificationGroup.service;

import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PaginatedResponse;
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

    /**
     * Retrieve all notification groups
     */
    PaginatedResponse<NotificationGroup> retrieveAll(String sortBy, Direction direction, int page, int itemsPerPage);

    /**
     *Update a notification group 
     */
    NotificationGroup update(int id, NotificationGroup notificationGroup);

}
