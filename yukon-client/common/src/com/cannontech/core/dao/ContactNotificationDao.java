package com.cannontech.core.dao;

import java.util.List;

import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteContactNotification;

public interface ContactNotificationDao {

    /**
     * Returns the LiteContactNotification for contactNotifID.
     * 
     */
    public LiteContactNotification getContactNotification(int contactNotifID);

    /**
     * Returns all contactNotifications.
     *
     */
    public List<LiteContactNotification> getAllContactNotifications();

    /**
     * Returns the parent Contact for this ContactNotifcation
     *
     */
    public LiteContact getContactNotificationsParent(int notifCatID);

}