package com.cannontech.core.dao;

import java.util.List;

import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteContactNotification;

public interface ContactNotificationDao {

    /**
     * Returns the LiteContactNotification for contactNotifID.
     */
    public LiteContactNotification getContactNotification(int contactNotifID);

    /**
     * Returns all contactNotifications.
     */
    public List<LiteContactNotification> getAllContactNotifications();

    /**
     * Returns the parent Contact for this ContactNotifcation
     */
    public LiteContact getContactNotificationsParent(int notifCatID);

    /**
     * Method to get a list of contact notifications for a given contact
     * @param contactId - Contact in question
     * @return List of notifications
     */
    public List<LiteContactNotification> getNotificationsForContact(
            int contactId);

    /**
     * Method to save a list of contact notifications for a contact (will remove
     * any notifications for that contact that are not in the list)
     * @param contactId - Id of contact to save notifications for
     * @param notification - Notification to save
     */
    public void saveNotificationsForContact(int contactId,
            List<LiteContactNotification> notificationList);

    /**
     * Method to save a contact notification
     * @param notification - Notification to save
     */
    public void saveNotification(LiteContactNotification notification);

    /**
     * Method to remove a contact notificaiton
     * @param notificationId - Id of notification to remove
     */
    public void removeNotification(int notificationId);
    
    /**
     * Method to remove all contact notificaitons for a contact
     * @param contactId - Id of contact to remove notificaitons for
     */
    public void removeNotificationsForContact(int contactId);

}