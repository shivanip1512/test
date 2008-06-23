package com.cannontech.stars.xml.serialize;

import java.util.Enumeration;
import java.util.Vector;

public abstract class StarsCustomerContact {
    private int contactId;
    private boolean hasContactId;
    private String lastName;
    private String firstName;
	private int loginID;
    private Vector<ContactNotification> contactNotificationList;

    public StarsCustomerContact() {
        contactNotificationList = new Vector<ContactNotification>();
    }

    public void addContactNotification(ContactNotification vContactNotification) {
        contactNotificationList.addElement(vContactNotification);
    } 

    public void addContactNotification(int index, ContactNotification vContactNotification) {
        contactNotificationList.insertElementAt(vContactNotification, index);
    }

    public void deleteContactID() {
        this.hasContactId= false;
    } 

    public Enumeration<ContactNotification> enumerateContactNotification() {
        return contactNotificationList.elements();
    } 

    public int getContactID() {
        return this.contactId;
    } 

	public int getLoginID() {
		return this.loginID;
	} 

    public ContactNotification getContactNotification(int index) {
        //-- check bounds for index
        if ((index < 0) || (index > contactNotificationList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (ContactNotification) contactNotificationList.elementAt(index);
    }

    public ContactNotification[] getContactNotification() {
        int size = contactNotificationList.size();
        ContactNotification[] mArray = new ContactNotification[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = contactNotificationList.elementAt(index);
        }
        return mArray;
    }

    public int getContactNotificationCount() {
        return contactNotificationList.size();
    } 

    public String getFirstName() {
        return this.firstName;
    } 

    public String getLastName() {
        return this.lastName;
    } 

    public boolean hasContactID() {
        return this.hasContactId;
    } 

    public void removeAllContactNotification() {
        contactNotificationList.removeAllElements();
    } 

    public ContactNotification removeContactNotification(int index) {
        ContactNotification obj = contactNotificationList.elementAt(index);
        contactNotificationList.removeElementAt(index);
        return obj;
    } 

    public void setContactID(int contactID) {
        this.contactId = contactID;
        this.hasContactId = true;
    } 

	public void setLoginID(int loginID) {
		this.loginID = loginID;
	} 

    public void setContactNotification(int index, ContactNotification vContactNotification) {
        //-- check bounds for index
        if ((index < 0) || (index > contactNotificationList.size())) {
            throw new IndexOutOfBoundsException();
        }
        contactNotificationList.setElementAt(vContactNotification, index);
    } 

    public void setContactNotification(ContactNotification[] contactNotificationArray) {
        //-- copy array
        contactNotificationList.removeAllElements();
        for (int i = 0; i < contactNotificationArray.length; i++) {
            contactNotificationList.addElement(contactNotificationArray[i]);
        }
    } 

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    } 

    public void setLastName(java.lang.String lastName) {
        this.lastName = lastName;
    } 

}
