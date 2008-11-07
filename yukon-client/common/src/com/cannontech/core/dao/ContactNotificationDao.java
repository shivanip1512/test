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

    /**
     * Method to return the lite notification for a contact of a specific type.
     * Returns null if none exists for that type.
     * Example usage: getNotificationForContactByType(contact.getContactId(), YukonListEntryTypes.YUK_ENTRY_ID_HOME_PHONE)
     * would return the home phone for that contact.
     * @param contactId
     * @param type
     * @return
     */
    public LiteContactNotification getNotificationForContactByType(int contactId, int type);

    /**
     * Method to return the contact notification ids for a contact
     * @param contactId
     * @return
     */
    public List<Integer> getNotificationIdsForContact(int contactId);

    /**
     * This method returns all the notification ids for the contacts provided.
     * Useful when bulk deleting contacts.
     * @param contactIds
     * @return
     */
    public List<Integer> getAllNotificationIdsForContactIds(List<Integer> contactIds);

    /**
     * Method to delete the notifications for a list of contacts
     * @param contactIds
     */
    public void removeNotificationsForContactIds(List<Integer> contactIds);

    /**
     * Method to delete notif destinations for a list of contact notifs.
     * @param contactNotifIds
     */
    public void removeContactNotifDestinationsForNotifs(List<Integer> contactNotifIds);

    /**
     * Method to delete notifs for a list of contact ids.
     * @param contactIds
     */
    public void removeContactNotifsForContactIds(List<Integer> contactIds);

    /**
     * Method to delete notif map entries for a list of contact ids.
     * @param contactIds
     */
    public void removeContactNotifMapEntriesForContactIds(List<Integer> contactIds);

}