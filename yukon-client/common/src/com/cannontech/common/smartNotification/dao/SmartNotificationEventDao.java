package com.cannontech.common.smartNotification.dao;

import com.cannontech.common.smartNotification.model.SmartNotificationEvent;

/**
 * This dao handles saving and loading Smart Notification events.
 */
public interface SmartNotificationEventDao {
    
    /**
     * Insert an event into the database.
     */
    void save(SmartNotificationEvent event);
    
}
