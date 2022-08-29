package com.cannontech.web.notificationGroup;

import java.util.List;

import com.cannontech.common.device.port.DBPersistentConverter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
@JsonPropertyOrder({ "id", "name", "emailEnabled", "phoneCallEnabled", "selected", "notifications" })
public class Contact extends NotificationSettings implements DBPersistentConverter<com.cannontech.database.db.contact.Contact> {
    private List<NotificationSettings> notifications;
    private String name;

    public Contact() {
        super();
    }

    public Contact(int id) {
        super(id);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Contact(int contactID, String name, boolean emailEnabled, boolean phoneCallEnabled) {
        super(contactID, emailEnabled, phoneCallEnabled);
        this.name = name;
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
