package com.cannontech.core.dao;

import java.util.List;
import java.util.Map;

import com.cannontech.database.data.lite.LiteCICustomer;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteContactNotification;
import com.cannontech.database.data.lite.LiteCustomer;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface ContactDao {

    /**
     * Returns the LiteContact for contactID_.
     * @return com.cannontech.database.data.lite.LiteContact
     * @param contactID_ int
     */
    public LiteContact getContact(int contactID_);
    
    public Map<Integer, LiteContact> getContacts(List<Integer> contactIds);

    /**
     * Returns the LiteContactNotification of the specified category
     * @param liteContact com.cannontech.database.data.lite.LiteContact
     * @param notifCatID int
     * @return com.cannontech.database.data.lite.LiteContactNotification
     */
    public LiteContactNotification getContactNotification(
            LiteContact liteContact, int notifCatID);

    /**
     * Returns the LiteContact for firstName_.
     * @return com.cannontech.database.data.lite.LiteContact
     * @param contactID_ int
     */
    public LiteContact[] getContactsByFName(String firstName_);

    /**
     * Returns the LiteContact for lastName_.
     * @return com.cannontech.database.data.lite.LiteContact
     * @param contactID_ int
     * @param partialMatch If true, lastName_ is to be matched partially from the first character
     */
    public LiteContact[] getContactsByLName(String lastName_,
            boolean partialMatch);

    /**
     * Returns an array of contactIDS having lastName as partialMatch (true) or exact match (partialMatch=false).
     * If lastName contains a comma, then the text following the comma is used for FIRSTNAME PARTIAL match 
     * @param lastName_
     * @param partialMatch
     * @return
     */
    public int[] retrieveContactIDsByLastName(String lastName_,
            boolean partialMatch);

    public LiteContact[] getContactsByLName(String lastName_);

    /**
     * @param phoneNo
     * @param phoneNotifCatIDs Array of notification category IDs, currently the options are:
     * YukonListEntryTypes.YUK_ENTRY_ID_PHONE, YUK_ENTRY_ID_HOME_PHONE, YUK_ENTRY_ID_WORK_PHONE,
     * @param partialMatch If true, phoneNo is to be matched partially from the last digit
     * @return Array of LiteContact for phoneNo
     */
    public LiteContact[] getContactsByPhoneNo(String phoneNo,
            int[] phoneNotifCatIDs, boolean partialMatch);

    public LiteContact[] getContactsByPhoneNo(String phoneNo,
            int[] phoneNotifCatIDs);

    /**
     * Returns the LiteContact for email_.
     * @return com.cannontech.database.data.lite.LiteContact
     * @param contactID_ int
     */
    public LiteContact getContactByEmailNotif(String email_);

    /**
     * Returns all the LiteContacts that do not belong to a Customer.
     * 
     */
    public LiteContact[] getUnassignedContacts();

    /**
     * Looks the first email notificatoin type in the list passed in. Returns a zero length string
     * when no emails are found.
     * @param contact
     * @return int
     */
    public String[] getAllEmailAddresses(int contactID_);

    /**
     * Returns all the PIN entries found for a given contact. If no
     * PIN entries are found, we return a zero length String array.
     * 
     */
    public LiteContactNotification[] getAllPINNotifDestinations(int contactID_);

    /**
     * Finds all notifcations that are of a phone type. Returns a zero length array
     * when no phone numbers are found.
     * 
     * @param contact
     * @return int
     */
    public LiteContactNotification[] getAllPhonesNumbers(int contactID_);

    /**
     * Returns all contactNotifications.
     * @return List LiteContactNotifications
     */
    public List<LiteContactNotification> getAllContactNotifications();

    /**
     * Returns the LiteCICustomer for addltContactID_.
     * @param addtlContact_ int
     * @return LiteCICustomer
     */
    public LiteCICustomer getOwnerCICustomer(int addtlContactID_);

    /**
     * Returns the LiteCICustomer for primaryContactID_.
     * @param primaryContact_ int
     * @return LiteCICustomer
     */
    public LiteCICustomer getPrimaryContactCICustomer(int primaryContactID_);
    
    /**
     * Returns true if the provided contactid is found in the Customer table, 
     * PrimaryContactId column.
     * @param contactId
     * @return
     */
    public boolean isPrimaryContact(int contactId);

    /**
     * Returns the LiteCICustomer for contactID_.
     * If contactID_ not the primaryContactID, then check the CustomerAdditionalContact(s).
     * @param contact_ int
     * @return LiteCICustomer
     */
    public LiteCICustomer getCICustomer(int contactID_);

    /**
     * Returns the LiteCustomer for contact ID
     * @param contactID int
     * @return LiteCustomer
     */
    public LiteCustomer getCustomer(int contactID);

    public LiteYukonUser getYukonUser(int contactID_);

    public boolean hasPin(int contactId);

    /**
     * Method to get the primary contact for an account
     * @param accountId - Id of account in question
     * @return The primary contact
     */
    public LiteContact getPrimaryContactForAccount(int accountId);

    /**
     * Method to get a list of additional contacts for a customer
     * @param customerId - Id of customer in question
     * @return The additional contacts
     */
    public List<LiteContact> getAdditionalContactsForCustomer(int customerId);
    
    /**
     * Method to get a list of additional contacts for an account
     * @param accountId - Id of account in question
     * @return The additional contacts
     */
    public List<LiteContact> getAdditionalContactsForAccount(int accountId);

    /**
     * Method to save a contact
     * @param contact - Contact to save
     */
    public void saveContact(LiteContact contact);

    /**
     * Method to add an additional contact to a customer
     * @param contact - Contact to add
     * @param customer - Customer to add contact to
     */
    public void addAdditionalContact(LiteContact contact, LiteCustomer customer);

    /**
     * Method to remove an additional contact
     * @param contactId - Id of contact to remove
     */
    public void removeAdditionalContact(int contactId);

}