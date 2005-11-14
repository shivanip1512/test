package com.cannontech.database.data.customer;

import java.sql.SQLException;

import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.data.lite.LiteCICustomer;
import com.cannontech.database.data.lite.LiteCustomer;

/**
 * Insert the type's description here.
 * Creation date: (10/25/2001 12:31:55 PM)
 * @author: 
 */

public final class CustomerFactory 
{

	/**
	 * CustomerFactory constructor comment.
	 */
	public CustomerFactory() {
		super();
	}
	/**
	 * This method was created in VisualAge.
	 * @return com.cannontech.database.data.device.DeviceBase
	 * @param custType int
	 */
	public final static Customer createCustomer(int custType) 
	{
		Customer returnCustomer = null;
	
		switch( custType )
		{
			case CustomerTypes.CUSTOMER_RESIDENTIAL:
				returnCustomer = new Customer();
				break;

			case CustomerTypes.CUSTOMER_CI:
				returnCustomer = new CICustomerBase();
				break;
	
		}
	
		return returnCustomer;
	}
	
    /**
     * Gets the "chuby" representation of a Customer given its LiteCustomer object.
     * @param liteCustomer LiteCustomer for desired customer
     * @return corresponding Customer object
     * @throws SQLException
     * @throws TransactionException 
     */
    public final static Customer createCustomer(LiteCustomer liteCustomer) throws TransactionException {
        Customer newCustomer = new Customer();
        newCustomer.setCustomerID(new Integer(liteCustomer.getCustomerID()));
        Transaction.createTransaction(Transaction.RETRIEVE, newCustomer).execute();
        return newCustomer;
    }
    
    /**
     * Gets the "chuby" representation of a CICustomer given its LiteCICustomer object.
     * @param liteCiCustomer LiteCICustomer for desired customer
     * @return corresponding CICustomerBase object
     * @throws SQLException
     * @throws TransactionException 
     */
    public final static CICustomerBase createCustomer(LiteCICustomer liteCiCustomer) throws TransactionException {
        CICustomerBase newCustomer = new CICustomerBase();
        newCustomer.setCustomerID(new Integer(liteCiCustomer.getCustomerID()));
        Transaction.createTransaction(Transaction.RETRIEVE, newCustomer).execute();
        return newCustomer;
    }
	
}
