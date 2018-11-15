package com.cannontech.core.dao;

import java.util.List;
import java.util.Map;

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
    List<LiteContact> getAllContacts(LiteCustomer customer);
    
    LiteContact getPrimaryContact(int customerId);
    
    /**
     * Method to get a customer by id
     * @param customerId - Id of customer to get
     * @return Customer
     */
    LiteCustomer getLiteCustomer(int customerId);
    
    /**
     * Get a customer information by id
     * @param customerId - Id of customer to get
     * @return the customer information
     */
    CustomerInformation getCustomerInformation(int customerId);
    
    /**
     * Returns the lite cicustomer with the given customer id.
     * @param customerId
     * @return LiteCICustomer
     */
    LiteCICustomer getLiteCICustomer(int customerId);
    
    /**
     * Method to get the commercial customer for a user
     * @param user - User in question
     * @return - User's commercial customer
     */
    LiteCICustomer getCICustomerForUser(LiteYukonUser user);
    
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
    LiteCustomer getCustomerForUser(int userId);
    
    void callbackWithAllCiCustomers(final SimpleCallback<LiteCICustomer> callback);
    
    /**
     * Method to save the temperature unit 'F' or 'C' for this customer.
     * 
     * @param customerId
     * @param temp
     * @throws InvalidAttributeValueException 
     */
    void setTemperatureUnit(int customerId, String temp) throws IllegalArgumentException;
    
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
    void deleteCustomer(int customerId);
    
    /**
     * Returns true if this customer is a commercial/industrial customer, false otherwise
     * @param customerId
     * @return
     */
    boolean isCICustomer(int customerId);
    
    /**
     * Method to delete commercial/industrial customer info
     * @param customerId
     */
    void deleteCICustomer(int customerId);
    
    /**
     * Method to update a lite customer
     * @param customer
     * @return
     */
    void updateCustomer(LiteCustomer customer);
    
    /**
     * Method to update a lite commercial/industrial customer
     * @param customer
     * @return
     */
    void updateCICustomer(LiteCICustomer customer);
    
    /**
     * Method to return the address id of a commercial/industrial customer
     * @param customerId
     * @return
     */
    int getAddressIdForCICustomer(int customerId);
    
    int getAllCiCustomerCount();

    /** 
     * Returns the customer with the given primary contact 
     * or null if none exist with that primary contact. 
     */
    LiteCustomer getLiteCustomerByPrimaryContact(int primaryContactId);

    /**
     * Returns a map of customers with empty altTrackNum for list of customerIds
     */
    Map<Integer, LiteCustomer> getCustomersWithEmptyAltTrackNum(List<Integer> customerIds);

}