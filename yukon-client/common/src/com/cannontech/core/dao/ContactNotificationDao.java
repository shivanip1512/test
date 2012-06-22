package com.cannontech.core.dao;

import java.util.List;

import com.cannontech.common.model.ContactNotificationType;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteContactNotification;

public interface ContactNotificationDao {

	/**
	 * @deprecated Use {@link #getNotificationForContact(int)}
	 */
    public LiteContactNotification getContactNotification(int contactNotifID);
    
    public LiteContactNotification getNotificationForContact(int notificationId);

    /**
	 * @deprecated Use {@link #getNotificationsForContact(int)}
	 */
    public List<LiteContactNotification> getAllContactNotifications();

    /**
     * Method to get a list of contact notifications for a given contact
     * @param contactId - Contact in question
     * @return List of notifications
     */
    public List<LiteContactNotification> getNotificationsForContact(int contactId);
    
    public List<LiteContactNotification> getNotificationsForContact(LiteContact liteContact);
    
    public List<LiteContactNotification> getNotificationsForContactByType(int contactId, ContactNotificationType contactNotificationType);
    
    public List<LiteContactNotification> getNotificationsForContactByType(LiteContact liteContact, ContactNotificationType contactNotificationType);

    /**
     * Method to return the lite notification for a contact of a specific type.
     * Returns null if none exists for that type.
     * Example usage: getFirstNotificationForContactByType(contact, YukonListEntryTypes.YUK_ENTRY_ID_HOME_PHONE)
     * would return the home phone for that contact.
     * @param contact
     * @param type
     * @return
     */
    public LiteContactNotification getFirstNotificationForContactByType(LiteContact liteContact, ContactNotificationType contactNotificationType);
    
    /**
     * Method to return the lite notification for a contact of a specific type.
     * Returns null if none exists for that type.
     * Example usage: getFirstNotificationForContactByType(contactId, YukonListEntryTypes.YUK_ENTRY_ID_HOME_PHONE)
     * would return the home phone for that contact.
     */
    public LiteContactNotification getFirstNotificationForContactByType(int contactId, ContactNotificationType contactNotificationType);

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
    
    public void removeNotifications(List<Integer> notificationIds);
    
    /**
     * Method to return the contact notification ids for a contact
     * @param contactId
     * @return
     */
    public List<Integer> getNotificationIdsForContact(int contactId);

    /**
     * Method to get a list of contact notifications for a notification of a specific type.
     * @param notification
     * @param contactNotificationType
     * @return List of notifications
     */
    public List<LiteContactNotification> getNotificationsForNotificationByType(String notification,
                                                                  ContactNotificationType contactNotificationType);
}