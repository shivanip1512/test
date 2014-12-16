package com.cannontech.database.data.lite;

import com.cannontech.common.model.ContactNotificationType;

public class LiteContactNotification extends LiteBase {
    private int contactID = 0;
    private int notificationCategoryID = ContactNotificationType.HOME_PHONE.getDefinitionId(); /* Home Phone category */
    private String disableFlag = null;
    private String notification = null;

    private int order = 0;

    public LiteContactNotification() {
        super();
        setLiteType(LiteTypes.CONTACT_NOTIFICATION);
    }

    public LiteContactNotification(int contactNotificationId) {
        super();
        setContactNotifID(contactNotificationId);
        setLiteType(LiteTypes.CONTACT_NOTIFICATION);
    }

    public LiteContactNotification(int contactNotificationId, int contactId, int notificationCategoryId, 
            String disableFlag, String notification) {
        this(contactNotificationId);
        setContactID(contactId);
        setNotificationCategoryID(notificationCategoryId);
        setDisableFlag(disableFlag);
        setNotification(notification);
    }

    public int getContactNotifID() {
        return getLiteID();
    }

    public int getContactID() {
        return contactID;
    }

    public void setContactID(int newContactID) {
        contactID = newContactID;
    }

    public void setContactNotifID(int newID) {
        setLiteID(newID);
    }

    @Override
    public String toString() {
        return getNotification();
    }

    public String getDisableFlag() {
        return disableFlag;
    }

    public boolean isDisabled() {
        return "y".equalsIgnoreCase(getDisableFlag());
    }

    public String getNotification() {
        return notification;
    }

    public int getNotificationCategoryID() {
        return notificationCategoryID;
    }

    public void setDisableFlag(String disableFlag) {
        this.disableFlag = disableFlag;
    }

    public void setNotification(String notification) {
        this.notification = notification;
    }

    public void setNotificationCategoryID(int notificationCategoryID) {
        this.notificationCategoryID = notificationCategoryID;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public ContactNotificationType getContactNotificationType() {
        return ContactNotificationType.getTypeForNotificationCategoryId(this.getNotificationCategoryID());
    }
}
