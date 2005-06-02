package com.cannontech.notif.outputs;

import java.util.*;

import com.cannontech.database.cache.functions.*;
import com.cannontech.database.data.lite.*;
import com.cannontech.database.data.notification.*;

/**
 * This class represents a single entity that needs to be reached
 * to deliver a notification. This entity could originate as a 
 * customer, a contact, or a single email address or phone number.
 * The Contactable class implements the Callable and Emailable 
 * interfaces that provide an ordered list of phone numbers and
 * email address respectively for the entity.
 * It is possible to create a Contactable object that would return
 * zero phone numbers and email addresses.
 */
public class Contactable implements Callable, Emailable {
    public static final String EMAIL = "email";
    public static final String VOICE = "voice";
    private List _contactPhoneNumberList = new LinkedList();
    private List _emailList = new LinkedList();
    private String _label;
    private TimeZone _timeZone;

    /**
     * Create a Contactable object from a CustomerNotifGroupMap.
     * @param customerGroupMap
     */
    public Contactable(CustomerNotifGroupMap customerGroupMap) {
        List contacts = CustomerFuncs.getAllContacts(customerGroupMap.getCustomerID());
        for (Iterator iter = contacts.iterator(); iter.hasNext();) {
            LiteContact contact = (LiteContact) iter.next();
            if (customerGroupMap.isSendOutboundCalls()) {
                addPhoneNumbersForContact(contact);
            }
            if (customerGroupMap.isSendEmails()) {
                addEmailsForContact(contact);
            }
        }
        
        LiteCustomer liteCustomer = CustomerFuncs.getLiteCustomer(customerGroupMap.getCustomerID());
        String tzString = liteCustomer.getTimeZone();
        _timeZone = TimeZone.getTimeZone(tzString);
        
        _label = "Customer " + customerGroupMap.getCustomerID();
    }

    /**
     * Create a Contactable object from a ContactNotifGroupMap.
     * @param notifGroupMap
     */
    public Contactable(ContactNotifGroupMap notifGroupMap) {
        LiteContact contact = ContactFuncs.getContact(notifGroupMap.getContactID());
        if (notifGroupMap.isSendOutboundCalls()) {
            addPhoneNumbersForContact(contact);
        }
        if (notifGroupMap.isSendEmails()) {
            addEmailsForContact(contact);
        }
        
        //TODO how to determine time zone
        _timeZone = TimeZone.getDefault();
        
        _label = "Contact " + contact.toString();
    }

    /**
     * Create a Contactable object from a NotifDestinationMap.
     * @param destinationMap
     */
    public Contactable(NotifDestinationMap destinationMap) {
        LiteContactNotification notification = ContactNotifcationFuncs.getContactNotification(destinationMap.getRecipientID());
        if (YukonListFuncs.isPhoneNumber(notification.getNotificationCategoryID())
                && destinationMap.isSendOutboundCalls()) {
            addPhoneNumber(notification);
        } else if (YukonListFuncs.isEmail(notification.getNotificationCategoryID())
                && destinationMap.isSendEmails()) {
            _emailList.add(notification.getNotification());
        }
        
        //TODO how to determine time zone
        _timeZone = TimeZone.getDefault();
        
        _label = notification.getNotification();
    }
    
    private void addPhoneNumbersForContact(LiteContact contact) {
        LiteContactNotification[] _rawNumbers = ContactFuncs.getAllPhonesNumbers(contact.getContactID());
        for (int i = 0; i < _rawNumbers.length; i++) {
            LiteContactNotification number = _rawNumbers[i];
            addPhoneNumber(number);
        }
    }

    private void addEmailsForContact(LiteContact contact) {
        String[] emails = ContactFuncs.getAllEmailAddresses(contact.getContactID());
        for (int i = 0; i < emails.length; i++) {
            String email = emails[i];
            _emailList.add(email);
        }
    }

    private void addPhoneNumber(LiteContactNotification number) {
        ContactPhone phoneObject = new ContactPhone(number.getNotification(),number.getContactID());
        _contactPhoneNumberList.add(phoneObject);

    }

    /**
     * @return An immutable List of ContactPhone objects.
     */
    public List getContactPhoneNumberList() {
        return Collections.unmodifiableList(_contactPhoneNumberList);
    }

    /**
     * @return An immutable List of Strings
     */
    public List getEmailList() {
        return Collections.unmodifiableList(_emailList);

    }

    /**
     * Determines if this Contactable has the bit corresponding to the specified
     * output type set.
     * @param type {email, voice}
     * @return True if this object should be notified by the specified method
     * @throws IllegalArgumentException If type is not "email" or "voice"
     */
    public boolean hasNotificationMethod(String type) {
        if (type.equals(EMAIL)) {
            return _emailList.size() > 0;
        } else if (type.equals(VOICE)) {
            return _contactPhoneNumberList.size() > 0;
        } else {
            throw new IllegalArgumentException("Unknown output type");
        }
    }
    
    public String toString() {
        return _label;
    }
    
    public TimeZone getTimeZone() {
        return _timeZone;
    }

    /**
     * Returns a list of Contactables given a LiteNotificationGroup. The 
     * LiteNotificationGroup can contain LiteCustomers, LiteContacts, and
     * LiteNotifications. The resulting list will have one entry for each 
     * entry in the LiteNotificationGroup.
     * @param lng The LiteNotificationGroup to use
     * @return A list of Contactable objects
     */
    public static List getContactablesForGroup(LiteNotificationGroup lng) {
        LinkedList resultList = new LinkedList();
        CustomerNotifGroupMap[] customerMap = lng.getCustomerMap();
        for (int i = 0; i < customerMap.length; i++) {
            CustomerNotifGroupMap notifGroupMap = customerMap[i];
            resultList.add(new Contactable(notifGroupMap));
        }

        ContactNotifGroupMap[] contactMap = lng.getContactMap();
        for (int i = 0; i < contactMap.length; i++) {
            ContactNotifGroupMap notifGroupMap = contactMap[i];
            resultList.add(new Contactable(notifGroupMap));
        }

        NotifDestinationMap[] notifDestinationMap = lng.getNotifDestinationMap();
        for (int i = 0; i < notifDestinationMap.length; i++) {
            NotifDestinationMap destinationMap = notifDestinationMap[i];
            resultList.add(new Contactable(destinationMap));
        }
        
        return resultList;
    }

}
