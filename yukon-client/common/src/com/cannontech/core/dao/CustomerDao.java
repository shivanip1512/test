package com.cannontech.core.dao;

import java.util.List;

import javax.management.InvalidAttributeValueException;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;

import com.cannontech.common.util.SimpleCallback;
import com.cannontech.customer.model.CustomerInformation;
import com.cannontech.database.data.lite.LiteCICustomer;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteCustomer;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface CustomerDao {

    /**
     * Finds the customer contact for this user id
     * @param userID
     * @return List LiteContact
     */
    public List<LiteContact> getAllContacts(LiteCustomer customer);

    public LiteContact getPrimaryContact(int customerID_);

    /**
     * Method to get a customer by id
     * @param custID - Id of customer to get
     * @return Customer
     */
    public LiteCustomer getLiteCustomer(int custID);

    /**
     * Get a customer information by id
     * @param custID - Id of customer to get
     * @return the customer information
     */
    public CustomerInformation getCustomerInformation(int custID);

    /**
     * Finds all LiteContact instances not used by a CICustomer
     * @return LiteContact
     * 
     *
     public LiteContact[] getUnusedContacts( )
     {
     com.cannontech.database.cache.DefaultDatabaseCache cache = 
     com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
     
     ArrayList retValues = new ArrayList(50);
     
     synchronized(cache)	 
     {
     List customers = cache.getAllCICustomers();
     List contacts = cache.getAllContacts();
     
     for( int i = 0; i < contacts.size(); i++ ) 
     {
     LiteContact cnt = (LiteContact)contacts.get(i);
     boolean found = false;
     
     for( int j = 0; j < customers.size(); j++ )
     {				
     if( !((LiteCICustomer)customers.get(j)).getAdditionalContacts().contains(cnt) ) 
     continue;
     else
     {
     found = true;
     break;
     }
     }
     
     if( !found )
     retValues.add( cnt );
     
     }		
     }
     
     //sort the contacts
     java.util.Collections.sort( 
     retValues, 
     com.cannontech.database.data.lite.LiteComparators.liteStringComparator );
     
     LiteContact[] cnts = new LiteContact[ retValues.size() ];
     return (LiteContact[])retValues.toArray( cnts );
     }
     */
    /**
     * Returns the lite cicustomer with the given customer id.
     * @param customerID
     * @return LiteCICustomer
     */
    public LiteCICustomer getLiteCICustomer(int customerID);

    /**
     * Method to get the commercial customer for a user
     * @param user - User in question
     * @return - User's commercial customer
     */
    public LiteCICustomer getCICustomerForUser(LiteYukonUser user);
    
    /**
	 * Method to get the customer that a user is associated with
	 * 
	 * @param userId - User in question
	 * @return - User's customer
	 * 
	 * @throws IncorrectResultSizeDataAccessException
	 *             if user doesn't have a contact or the contact doesn't have a
	 *             customer.
	 */
    public LiteCustomer getCustomerForUser(int userId);
    
    public void callbackWithAllCiCustomers(final SimpleCallback<LiteCICustomer> callback);
    
    /**
     * Method to save the temperature unit 'F' or 'C' for this customer.
     * 
     * @param customerId
     * @param temp
     * @throws InvalidAttributeValueException 
     */
    public void setTemperatureUnit(int customerId, String temp) throws IllegalArgumentException;

    /**
     * Method to add a customer to the database
     * @param liteCustomer
     * @throws DataAccessException
     */
    void addCustomer(LiteCustomer liteCustomer) throws DataAccessException;

    /**
     * Method to add a commercial/industrial customer to the database
     * @param liteCICustomer
     * @throws DataAccessException
     */
    void addCICustomer(LiteCICustomer liteCICustomer) throws DataAccessException;

    /**
     * Method to delete a customer
     * @param customerId
     */
    public void deleteCustomer(Integer customerId);

    /**
     * Returns true if this customer is a commercial/industrial customer, false otherwise
     * @param customerId
     * @return
     */
    boolean isCICustomer(Integer customerId);

    /**
     * Method to delete commercial/industrial customer info
     * @param customerId
     */
    public void deleteCICustomer(Integer customerId);

    /**
     * Method to update a lite customer
     * @param customer
     * @return
     */
    public void updateCustomer(LiteCustomer customer);
    
    /**
     * Method to update a lite commercial/industrial customer
     * @param customer
     * @return
     */
    public void updateCICustomer(LiteCICustomer customer);

    /**
     * Method to return the address id of a commercial/industrial customer
     * @param customerId
     * @return
     */
    public int getAddressIdForCICustomer(int customerId);

    public int getAllCiCustomerCount();

}