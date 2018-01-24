package com.cannontech.common.smartNotification.service;

import com.cannontech.common.smartNotification.model.SmartNotificationEventType;
import com.cannontech.common.smartNotification.model.SmartNotificationSubscription;
import com.cannontech.user.YukonUserContext;

/**
 * Service for saving and retrieving users' Smart Notification subscriptions.
 */
public interface SmartNotificationSubscriptionService {
        
    /**
     * deletes subscription
     */
    void deleteSubscription(int id, YukonUserContext userContext);
    
    /**
     * If id is 0 creates subscription otherwise updates subscription.
     */
    int saveSubscription(SmartNotificationSubscription subscription, YukonUserContext userContext);

    /**
     * Delete all subscriptions with the specified type and identifier. The friendly name and user are supplied for 
     * event logging.
     */
    void deleteSubscriptions(SmartNotificationEventType type, String identifier, String name, YukonUserContext userContext);
    
}

