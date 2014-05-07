package com.cannontech.database.data.customer;

import java.util.Vector;

import com.cannontech.common.editor.EditorPanel;
import com.cannontech.database.SqlStatement;
import com.cannontech.database.db.CTIDbChange;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.db.NestedDBPersistent;
import com.cannontech.database.db.NestedDBPersistentComparators;
import com.cannontech.database.db.contact.ContactNotification;
import com.cannontech.database.db.customer.Address;
import com.cannontech.database.db.customer.Customer;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;

public class Contact extends DBPersistent implements CTIDbChange, EditorPanel, IAddress {
    private com.cannontech.database.db.contact.Contact customerContact = null;

    private Address address = null;
    private Vector<ContactNotification> contactNotifVect = null;

    public Contact() {
    }

    public Contact(Integer contactID) {
        setContactID(contactID);
    }

    @Override
    public void add() throws java.sql.SQLException {
        // be sure all DB objects have this ID set
        if (getContact().getContactID() == null) {
            setContactID(com.cannontech.database.db.contact.Contact.getNextContactID());
        }

        // be sure all or our objects share the same contactID
        setContactID(getContact().getContactID());

        getAddress().add();
        getContact().setAddressID(getAddress().getAddressID());

        getContact().add();

        for (int i = 0; i < getContactNotifVect().size(); i++) {
            ((DBPersistent) getContactNotifVect().get(i)).add();
        }
    }

    @Override
    public void delete() throws java.sql.SQLException {
        ContactNotification.deleteAllContactNotifications(getDbConnection(), getContact().getContactID().intValue());

        getAddress().setAddressID(getContact().getAddressID());

        delete("ContactNotifGroupMap", "ContactID", getContact().getContactID());

        getContact().delete();

        if (getAddress().getAddressID().intValue() != com.cannontech.common.util.CtiUtilities.NONE_ZERO_ID) {
            getAddress().delete();
        }
    }

    public Vector<ContactNotification> getContactNotifVect() {
        if (contactNotifVect == null) {
            contactNotifVect = new Vector<ContactNotification>(8);
        }

        return contactNotifVect;
    }

    public com.cannontech.database.db.contact.Contact getContact() {
        if (customerContact == null) {
            customerContact = new com.cannontech.database.db.contact.Contact();
        }

        return customerContact;
    }

    @Override
    public Address getAddress() {
        if (address == null) {
            address = new Address();
        }

        return address;
    }

    @Override
    public DBChangeMsg[] getDBChangeMsgs(DbChangeType dbChangeType) {

        DBChangeMsg[] msgs =
            { new DBChangeMsg(getContact().getContactID().intValue(), DBChangeMsg.CHANGE_CONTACT_DB,
                DBChangeMsg.CAT_CUSTOMERCONTACT, DBChangeMsg.CAT_CUSTOMERCONTACT, dbChangeType) };

        return msgs;
    }

    @Override
    public void retrieve() throws java.sql.SQLException {
        getContact().retrieve();

        getAddress().setAddressID(getContact().getAddressID());
        getAddress().retrieve();

        ContactNotification[] cntNotifs =
            ContactNotification.getContactNotifications(getDbConnection(), getContact().getContactID().intValue());

        for (int i = 0; i < cntNotifs.length; i++) {
            getContactNotifVect().add(cntNotifs[i]);
        }
    }

    public void setContactID(Integer contactID) {
        getContact().setContactID(contactID);

        for (int i = 0; i < getContactNotifVect().size(); i++) {
            getContactNotifVect().get(i).setContactID(contactID);
        }
    }

    public void setCustomerContact(com.cannontech.database.db.contact.Contact newCustomerContact) {
        customerContact = newCustomerContact;
    }

    @Override
    public void setDbConnection(java.sql.Connection conn) {
        super.setDbConnection(conn);

        getAddress().setDbConnection(conn);

        getContact().setDbConnection(conn);

        for (int i = 0; i < getContactNotifVect().size(); i++) {
            ((DBPersistent) getContactNotifVect().get(i)).setDbConnection(conn);
        }
    }

    @Override
    public String toString() {
        if (getContact() != null) {
            return getContact().getContLastName() + ", " + getContact().getContFirstName();
        }
        return null;
    }

    @Override
    public void update() throws java.sql.SQLException {
        getAddress().setAddressID(getContact().getAddressID());
        getAddress().update();

        // be sure all or our objects share the same contactID
        setContactID(getContact().getContactID());

        getContact().update();

        // grab all the previous gear entries for this program
        Vector<ContactNotification> oldContactNotifies =
            ContactNotification.getContactNotifications(this.getContact().getContactID().intValue(), getDbConnection());

        // run all the ContactNotifications through the NestedDBPersistent comparator
        // to see which ones need to be added, updated, or deleted.
        Vector newVect =
            NestedDBPersistentComparators.NestedDBPersistentCompare(oldContactNotifies, getContactNotifVect(),
                NestedDBPersistentComparators.contactNotificationComparator);

        // throw the gears into the Db
        for (int i = 0; i < newVect.size(); i++) {
            ((NestedDBPersistent) newVect.elementAt(i)).setDbConnection(getDbConnection());
            ((NestedDBPersistent) newVect.elementAt(i)).executeNestedOp();
        }

    }

    public final static boolean isPrimaryContact(Integer contactID, String databaseAlias) {
        SqlStatement stmt = new SqlStatement("SELECT PrimaryContactID FROM " + Customer.TABLE_NAME
            + " WHERE PrimaryContactID=" + contactID, databaseAlias);

        try {
            stmt.execute();
            return (stmt.getRowCount() > 0);
        } catch (Exception e) {
            return false;
        }
    }

    public void setAddress(Address address) {
        this.address = address;
    }
}
