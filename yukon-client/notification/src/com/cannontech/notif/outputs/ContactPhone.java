package com.cannontech.notif.outputs;

import com.cannontech.notif.voice.PhoneNumber;

public class ContactPhone {
    PhoneNumber _phoneNumber;
    int contactId; // this could be a LiteContact
    
    public ContactPhone(PhoneNumber number, int id) {
        _phoneNumber = number;
        contactId = id;
    }
    
    public ContactPhone(String number, int id) {
        _phoneNumber = new PhoneNumber(number);
        contactId = id;
    }
    
    public int getContactId() {
        return contactId;
    }
    
    public PhoneNumber getPhoneNumber() {
        return _phoneNumber;
    }
}
