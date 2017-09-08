package com.cannontech.common.smartNotification.service;

import com.cannontech.common.smartNotification.model.SmartNotificationEvent;
import com.cannontech.common.smartNotification.model.SmartNotificationEventMulti;

/**
 * Sends Smart Notification events to be recorded and processed. 
 */
public interface SmartNotificationEventCreationService {
    
    /**
     * Send a Smart Notification event via the event queue.
     */
    void sendEvent(SmartNotificationEvent event);
    
    /**
     * Send multiple Smart Notification events in a single object.
     */
    void sendEvents(SmartNotificationEventMulti multi);
}