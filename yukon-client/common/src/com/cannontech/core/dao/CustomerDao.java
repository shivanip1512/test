package com.cannontech.core.dao;

import java.util.List;
import java.util.Vector;

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
    public List<LiteContact> getAllContacts(int customerID_);

    public LiteContact getPrimaryContact(int customerID_);

    /**
     * Method to get a customer by id
     * @param custID - Id of customer to get
     * @return Customer
     */
    public LiteCustomer getLiteCustomer(int custID);

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
     * Returns a vector of LiteCustomers with given energyCompanyID
     * @param energycompanyID
     * @return Vector liteCustomers
     */
    public Vector<LiteCustomer> getAllLiteCustomersByEnergyCompany(int energyCompanyID);

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
    
    /**
     * Method to save the temperature unit 'F' or 'C' for this customer.
     * 
     * @param customerId
     * @param temp
     */
    public void setTempForCustomer(int customerId, String temp);
}