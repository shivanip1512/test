package com.cannontech.common.smartNotification.dao;

import java.util.List;

import com.cannontech.common.smartNotification.model.SmartNotificationEventType;
import com.cannontech.common.smartNotification.model.SmartNotificationSubscription;

/**
 * Dao for saving and retrieving users' Smart Notification subscriptions.
 */
public interface SmartNotificationSubscriptionDao {
    
    /**
     * Deletes subscription.
     */
    void deleteSubscription(int id);
    
    /**
     * Returns subscription.
     */
    SmartNotificationSubscription getSubscription(int id);
    
    /**
     * Returns subscriptions by type.
     */
    List<SmartNotificationSubscription> getSubscriptions(SmartNotificationEventType type);
    
    /**
     * Returns subscriptions by type and user id.
     */
    List<SmartNotificationSubscription> getSubscriptions(int userId, SmartNotificationEventType type);

    /**
     * If id is 0 creates subscription otherwise saves subscription.
     */
    int saveSubscription(SmartNotificationSubscription subscription);
}
