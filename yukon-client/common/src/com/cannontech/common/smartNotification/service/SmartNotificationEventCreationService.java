package com.cannontech.common.smartNotification.service;

import com.cannontech.common.smartNotification.model.SmartNotificationEvent;

/**
 * Sends Smart Notification events to be recorded and processed. 
 */
public interface SmartNotificationEventCreationService {
    
    /**
     * Send a Smart Notification event via the event queue.
     */
    void sendEvent(SmartNotificationEvent event);
}