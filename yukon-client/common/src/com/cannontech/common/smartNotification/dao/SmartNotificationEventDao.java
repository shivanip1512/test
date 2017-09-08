package com.cannontech.common.smartNotification.dao;

import java.util.List;

import com.cannontech.common.smartNotification.model.SmartNotificationEvent;

/**
 * This dao handles saving and loading Smart Notification events.
 */
public interface SmartNotificationEventDao {
    
    /**
     * Insert events into the database.
     */
    void save(List<SmartNotificationEvent> event);
    
}
