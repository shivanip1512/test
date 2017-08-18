package com.cannontech.services.smartNotification.service;

import com.cannontech.common.smartNotification.model.SmartNotificationEvent;

/**
 * Services implementing this interface receive a specific type of SmartNotificationEvent and determine when to send
 * notifications, who to send them to, and how to send them. When the service determines that a notification should be
 * sent, it places a SmartNotificationMessageParameters object on the Smart Notification assembler queue for processing.
 */
public interface SmartNotificationDecider<T extends SmartNotificationEvent> {
    
    /**
     * Pull an event off the decider queue and determine if a notification needs to be sent.
     */
    public abstract void handle(T event);
    
}
