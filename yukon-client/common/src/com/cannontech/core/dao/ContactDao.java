package com.cannontech.core.dao;

import java.util.List;
import java.util.Map;

import com.cannontech.common.util.SimpleCallback;
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

    /**
     * Method to delete all addition contacts for a customer
     * @param customerId
     */
    public void deleteAllAdditionalContactsToCustomerReferences(Integer customerId);

    /**
     * Method to delete a contact
     * @param contactId
     */
    public void deleteContact(Integer contactId);

    /**
     * Method to delete all additional contacts for customer
     * @param customerId
     */
    public void deleteAllAdditionalContactsForCustomer(Integer customerId);

    /**
     * Method to get all contact ids for a customer
     * @param customerId
     * @return
     */
    List<Integer> getAdditionalContactIdsForCustomer(int customerId);

    /**
     * Method to delete a list of additional contacts.
     * @param contactIds
     */
    public void deleteAdditionalContactsByContactId(List<Integer> contactIds);

    /**
     * Method to delete a contact by id.
     * @param contactId
     */
    public void deleteContactById(Integer contactId);

    /**
     * Method to delete a list of contacts by ids.
     * @param contactIds
     */
    public void deleteContactsByIds(List<Integer> contactIds);

    /**
     * Method to delete an additional contact by id.
     * @param contactId
     */
    public void deleteAdditionalContactById(Integer contactId);

    /**
     * Method to retrieve a list of contacts for a given login id.
     * @param loginId
     * @return
     */
    public List<LiteContact> getContactsByLoginId(int loginId);

    public int getAllContactCount();
    
    public void callbackWithAllContacts(SimpleCallback<LiteContact> simpleCallback);

}