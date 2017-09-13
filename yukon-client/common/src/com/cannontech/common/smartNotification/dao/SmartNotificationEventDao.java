package com.cannontech.common.smartNotification.dao;

import java.util.List;
import java.util.Map;

import org.joda.time.Instant;

import com.cannontech.common.smartNotification.model.SmartNotificationEvent;
import com.cannontech.common.smartNotification.model.SmartNotificationEventType;
import com.cannontech.common.util.Range;

/**
 * This dao handles saving and loading Smart Notification events.
 */
public interface SmartNotificationEventDao {
    
    /**
     * Insert events into the database.
     */
    void save(SmartNotificationEventType type, List<SmartNotificationEvent> event);

    Map<Integer, SmartNotificationEvent> getEvents(SmartNotificationEventType type, Range<Instant> range);
    
}
