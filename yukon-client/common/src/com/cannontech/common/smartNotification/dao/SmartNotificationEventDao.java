package com.cannontech.common.smartNotification.dao;

import java.util.List;

import org.joda.time.Instant;

import com.cannontech.common.smartNotification.model.SmartNotificationEvent;
import com.cannontech.common.smartNotification.model.SmartNotificationEventType;
import com.cannontech.common.smartNotification.model.SmartNotificationFrequency;

/**
 * This dao handles saving and loading Smart Notification events.
 */
public interface SmartNotificationEventDao {
    
    /**
     * Insert events into the database.
     */
    void save(SmartNotificationEventType type, List<SmartNotificationEvent> event);
    
    /**
     * Returns events for type that have not been processed.
     */
    List<SmartNotificationEvent> getUnprocessedEvents(SmartNotificationEventType type);

    /**
     * Deletes all events. Used by simulator.
     */
    void deleteAllEvents();

    /**
     * Marks events as processed.
     */
    void markEventsAsProcessed(List<SmartNotificationEvent> events, Instant processedTime,
            SmartNotificationFrequency... frequency); 
}
