package com.cannontech.web.notificationGroup;

import java.util.List;

import com.cannontech.common.device.port.DBPersistentConverter;

public class Contact extends NotificationSettings implements DBPersistentConverter<com.cannontech.database.db.contact.Contact> {
    private List<NotificationSettings> notifications;

    public Contact() {
        super();
    }

    public Contact(int id, boolean emailEnabled, boolean phoneCallEnabled, boolean selected) {
        super(id, emailEnabled, phoneCallEnabled, selected);
    }

    public List<NotificationSettings> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<NotificationSettings> notifications) {
        this.notifications = notifications;
    }

    @Override
    public void buildModel(com.cannontech.database.db.contact.Contact contactBase) {
        setId(contactBase.getContactID());

    }

    @Override
    public void buildDBPersistent(com.cannontech.database.db.contact.Contact contactBase) {
        contactBase.setContactID(getId());
    }

}
