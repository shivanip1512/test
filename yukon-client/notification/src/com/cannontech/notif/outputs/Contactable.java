package com.cannontech.notif.outputs;

import java.util.*;

import com.cannontech.database.cache.functions.*;
import com.cannontech.database.data.lite.*;

/**
 * 
 */

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
    private List _contactPhoneNumberList;
    private List _emailList;
    private String _label;

    /**
     * Create a Contactable from a LiteCustomer.
     * @param customer the LiteCustomer to use
     */
    public Contactable(LiteCustomer customer) {
        List contacts = CustomerFuncs.getAllContacts(customer.getCustomerID());
        for (Iterator iter = contacts.iterator(); iter.hasNext();) {
            LiteContact contact = (LiteContact) iter.next();
            addPhoneNumbersForContact(contact);
            addEmailsForContact(contact);
        }
        _label = "Customer " + customer.getCustomerID();
    }

    /**
     * Create a Contactable from a LiteContact.
     * @param contact the LiteContact to use
     */
    public Contactable(LiteContact contact) {
        addPhoneNumbersForContact(contact);
        addEmailsForContact(contact);
        _label = "Contact " + contact.toString();
    }

    /**
     * Create a Contactable from a single email/phone of a
     * LiteContactNotification.
     * @param notification An email address or phone number.
     */
    public Contactable(LiteContactNotification notification) {
        if (YukonListFuncs.isPhoneNumber(notification.getNotificationCategoryID())) {
            addPhoneNumber(notification);
        } else if (YukonListFuncs.isEmail(notification.getNotificationCategoryID())) {
            _emailList.add(notification.getNotification());
        }
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
     * @return An immutable List of PhoneNumber objects.
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
     */
    public boolean hasNotificationMethod(String type) {
        return false; // TODO fix this
    }
    
    public String toString() {
        return _label;
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
        int[] customerIds = lng.getCustomerIDs();
        for (int i = 0; i < customerIds.length; i++) {
            int customerId = customerIds[i];
            LiteCustomer lCustomer = CustomerFuncs.getLiteCustomer(customerId);
            resultList.add(new Contactable(lCustomer));
        }
        
        int[] contactIds = lng.getContactIDs();
        for (int i = 0; i < contactIds.length; i++) {
            int contactId = contactIds[i];
            LiteContact lContact = ContactFuncs.getContact(contactId);
            resultList.add(new Contactable(lContact));
        }
        
        ArrayList notificationDestinations = lng.getNotificationDestinations();
        for (Iterator iter = notificationDestinations.iterator(); iter.hasNext();) {
            LiteContactNotification lNotification = (LiteContactNotification) iter.next();
            resultList.add(new Contactable(lNotification));
        }
        
        return resultList;
    }

}
