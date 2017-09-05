package com.cannontech.common.smartNotification.service;

import com.cannontech.common.smartNotification.model.SmartNotificationSubscription;

/**
 * Service for saving and retrieving users' Smart Notification subscriptions.
 */
public interface SmartNotificationSubscriptionService {
        
    /**
     * deletes subscription
     */
    void deleteSubscription(int id);
    
    /**
     * If id is 0 creates subscription otherwise updates subscription.
     */
    int saveSubscription(SmartNotificationSubscription subscription);
    
}

