package com.cannontech.common.smartNotification.service;

import com.cannontech.common.smartNotification.model.SmartNotificationEventMulti;

/**
 * Sends Smart Notification events to be recorded and processed. 
 */
public interface SmartNotificationEventCreationService {

    /**
     * Send multiple Smart Notification events in a single object.
     */
    void sendEvents(SmartNotificationEventMulti multi);
}