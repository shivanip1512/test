package com.cannontech.web.notificationGroup;

import java.util.List;

import com.cannontech.common.device.port.DBPersistentConverter;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.db.customer.CICustomerBase;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
@JsonPropertyOrder({ "id", "companyName", "emailEnabled", "phoneCallEnabled", "selected", "contacts" })
public class CICustomer extends NotificationSettings
        implements DBPersistentConverter<com.cannontech.database.db.customer.CICustomerBase> {
    private List<Contact> contacts;
    private String companyName = CtiUtilities.STRING_NONE;

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

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
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
