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
     * Returns the LiteContact for contactID_ or null (for default contactId=0).
     * Returns null if contactId is 0. This may happen when a customer's primaryContact is "not set" (value of 0 in DB).
     * @return com.cannontech.database.data.lite.LiteContact
     * @param contactID_ int
     */
    public LiteContact getContact(int contactID_);
    
    public Map<Integer, LiteContact> getContacts(List<Integer> contactIds);

    /**
     * @param phoneNo
     * @param partialMatch If true, phoneNo is to be matched partially from the last digit
     * @return Array of LiteContact for phoneNo
     */
    public List<LiteContact> findContactsByPhoneNo(String phoneNo, boolean partialMatch);

    /**
     * Returns the LiteContact for email.
     * If multiple contacts are found for the email, null is returned.
     * If no results are found for the email, null is returned.
     * @return com.cannontech.database.data.lite.LiteContact
     */
    public LiteContact findContactByEmail(String email);

    /**
     * Returns all the LiteContacts that do not belong to a Customer.
     * 
     */
    public List<LiteContact> getUnassignedContacts();

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
    
    public void associateAdditionalContact(int contactId, int customerId);

    public void deleteContact(LiteContact liteContact);
    
    /**
     * Method to get all contact ids for a customer
     * @param customerId
     * @return
     */
    List<Integer> getAdditionalContactIdsForCustomer(int customerId);

    /**
     * Method to retrieve a list of contacts for a given login id.
     * @param loginId
     * @return
     */
    public List<LiteContact> getContactsByLoginId(int loginId);

    public int getAllContactCount();
    
    public void callbackWithAllContacts(SimpleCallback<LiteContact> simpleCallback);
    
    /**
     * Get the default email address of the logged in user. 
     * @param userContext - Logged in user. 
     * @return email address - Logged in users email address.
     */
    String getUserEmail(LiteYukonUser  user);

}