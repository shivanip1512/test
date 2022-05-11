package com.cannontech.web.notificationGroup;

import java.util.List;

import com.cannontech.common.device.port.DBPersistentConverter;
import com.cannontech.database.db.customer.CICustomerBase;

public class CICustomer extends NotificationSettings
        implements DBPersistentConverter<com.cannontech.database.db.customer.CICustomerBase> {
    private List<Contact> contacts;

    public CICustomer() {
        super();
    }

    public CICustomer(int id) {
        super(id);
    }

    public List<Contact> getContacts() {
        return contacts;
    }

    public void setContacts(List<Contact> contacts) {
        this.contacts = contacts;
    }

    @Override
    public void buildModel(CICustomerBase cICustomerBase) {
        setId(cICustomerBase.getCustomerID());
    }

    @Override
    public void buildDBPersistent(CICustomerBase cICustomerBase) {
        cICustomerBase.setCustomerID(getId());
    }

}
