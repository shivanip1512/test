package com.cannontech.common.smartNotification.dao;

import java.util.List;

import com.cannontech.common.smartNotification.model.SmartNotificationEventType;
import com.cannontech.common.smartNotification.model.SmartNotificationFrequency;
import com.cannontech.common.smartNotification.model.SmartNotificationSubscription;
import com.google.common.collect.SetMultimap;

/**
 * Dao for saving and retrieving users' Smart Notification subscriptions.
 */
public interface SmartNotificationSubscriptionDao {
    
    /**
     * Deletes subscription.
     */
    void deleteSubscription(int id);
    
    /**
     * Delete all subscriptions with the specified type and reference value.
     * @return the number of subscriptions deleted.
     */
    int deleteSubscriptions(SmartNotificationEventType type, String value);
    
    /**
     * Returns subscription.
     */
    SmartNotificationSubscription getSubscription(int id);
    
    /**
     * Returns subscriptions by type.
     */
    List<SmartNotificationSubscription> getSubscriptions(SmartNotificationEventType type,
            SmartNotificationFrequency... frequency);
    
    /**
     * Returns subscriptions by type and user id.
     */
    List<SmartNotificationSubscription> getSubscriptions(int userId, SmartNotificationEventType type);

    /**
     * If id is 0 creates subscription otherwise updates subscription.
     */
    int saveSubscription(SmartNotificationSubscription subscription);

    /**
     * Returns subscriptions by user id.
     */
    List<SmartNotificationSubscription> getSubscriptions(int userId);

    /**
     * Deletes all subscriptions in the database. Used by simulator.
     */
    void deleteAllSubcriptions();

    /**
     * Returns all subscriptions.
     */
    List<SmartNotificationSubscription> getAllSubscriptions();

    /**
     * Returns subscriptions.
     * 
     * Examples of names and values
     *     name: monitorId values:1,2,3,4
     *     name: paoId values:2,3
     */
    List<SmartNotificationSubscription> getSubscriptions(SmartNotificationEventType type, String name,
            List<Object> values, SmartNotificationFrequency... frequency);

    SetMultimap<SmartNotificationEventType, SmartNotificationSubscription> getDailyDigestGrouped(String runTimeInMinutes);

    SetMultimap<SmartNotificationEventType, SmartNotificationSubscription> getDailyDigestUngrouped(String runTimeInMinutes);
    
    /**
     * Returns list of subscribed users for a smart notification event type
     */
    List<String> getSubscribedEmails(SmartNotificationEventType type);

    SetMultimap<Integer, SmartNotificationSubscription> getDailyDigestDeviceDataMonitorGrouped(String runTimeInMinutes);

    SetMultimap<Integer, SmartNotificationSubscription> getDailyDigestDeviceDataMonitorUngrouped(String runTimeInMinutes);
}
