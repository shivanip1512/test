package com.cannontech.common.smartNotification.service;

import java.util.List;

import com.cannontech.common.smartNotification.model.SmartNotificationEvent;
import com.cannontech.common.smartNotification.model.SmartNotificationEventType;

/**
 * Sends Smart Notification events to be recorded and processed. 
 */
public interface SmartNotificationEventCreationService {

    /**
     * Send multiple events.
     */
    void send(SmartNotificationEventType type, List<SmartNotificationEvent> events);
}