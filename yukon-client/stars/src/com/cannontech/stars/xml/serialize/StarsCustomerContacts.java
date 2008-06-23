package com.cannontech.stars.xml.serialize;

import java.util.Enumeration;
import java.util.Vector;

public abstract class StarsCustomerContacts {
    private PrimaryContact _primaryContact;
    private Vector<AdditionalContact> _additionalContactList;

    public StarsCustomerContacts() {
        _additionalContactList = new Vector<AdditionalContact>();
    }

    public void addAdditionalContact(AdditionalContact vAdditionalContact) {
        _additionalContactList.addElement(vAdditionalContact);
    } 

    public void addAdditionalContact(int index, AdditionalContact vAdditionalContact) {
        _additionalContactList.insertElementAt(vAdditionalContact, index);
    } 

    public Enumeration<AdditionalContact> enumerateAdditionalContact() {
        return _additionalContactList.elements();
    }

    public AdditionalContact getAdditionalContact(int index) {
        //-- check bounds for index
        if ((index < 0) || (index > _additionalContactList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return _additionalContactList.elementAt(index);
    }

    public AdditionalContact[] getAdditionalContact() {
        int size = _additionalContactList.size();
        AdditionalContact[] mArray = new AdditionalContact[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = _additionalContactList.elementAt(index);
        }
        return mArray;
    }

    public int getAdditionalContactCount() {
        return _additionalContactList.size();
    }

    public PrimaryContact getPrimaryContact() {
        return this._primaryContact;
    }

    public AdditionalContact removeAdditionalContact(int index) {
        AdditionalContact obj = _additionalContactList.elementAt(index);
        _additionalContactList.removeElementAt(index);
        return obj;
    } 

    public void removeAllAdditionalContact() {
        _additionalContactList.removeAllElements();
    }

    public void setAdditionalContact(int index, AdditionalContact vAdditionalContact) {
        //-- check bounds for index
        if ((index < 0) || (index > _additionalContactList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _additionalContactList.setElementAt(vAdditionalContact, index);
    }

    public void setAdditionalContact(AdditionalContact[] additionalContactArray) {
        //-- copy array
        _additionalContactList.removeAllElements();
        for (int i = 0; i < additionalContactArray.length; i++) {
            _additionalContactList.addElement(additionalContactArray[i]);
        }
    }

    public void setPrimaryContact(PrimaryContact primaryContact) {
        this._primaryContact = primaryContact;
    }

}
