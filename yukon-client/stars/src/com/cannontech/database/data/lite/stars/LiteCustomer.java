package com.cannontech.database.data.lite.stars;

import com.cannontech.database.Transaction;
import com.cannontech.database.data.customer.Customer;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.data.lite.LiteTypes;

/**
 * @author yao
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class LiteCustomer extends LiteBase {
	
	private int primaryContactID = 0;
	private int customerTypeID = com.cannontech.database.data.customer.CustomerTypes.INVALID;
	private String timeZone = null;
	private java.util.ArrayList additionalContacts = null;	// List of customer contact IDs

	public LiteCustomer() {
		super();
		setLiteType( LiteTypes.CUSTOMER );
	}
	
	public LiteCustomer(int customerID) {
		super();
		setCustomerID( customerID );
		setLiteType( LiteTypes.CUSTOMER );
	}
	
	public int getCustomerID() {
		return getLiteID();
	}
	
	public void setCustomerID(int customerID) {
		setLiteID( customerID );
	}
	
	public void retrieve() {
		Customer customer = new Customer();
		customer.setCustomerID( new Integer(getCustomerID()) );
		try {
			customer = (Customer) Transaction.createTransaction(Transaction.RETRIEVE, customer).execute();
			StarsLiteFactory.setLiteCustomer( this, customer );
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Returns the additionalContacts.
	 * @return java.util.ArrayList
	 */
	public java.util.ArrayList getAdditionalContacts() {
		if (additionalContacts == null)
			additionalContacts = new java.util.ArrayList(4);
		return additionalContacts;
	}

	/**
	 * Returns the customerTypeID.
	 * @return int
	 */
	public int getCustomerTypeID() {
		return customerTypeID;
	}

	/**
	 * Returns the primaryContactID.
	 * @return int
	 */
	public int getPrimaryContactID() {
		return primaryContactID;
	}

	/**
	 * Sets the additionalContacts.
	 * @param additionalContacts The additionalContacts to set
	 */
	public void setAdditionalContacts(java.util.ArrayList additionalContacts) {
		this.additionalContacts = additionalContacts;
	}

	/**
	 * Sets the customerTypeID.
	 * @param customerTypeID The customerTypeID to set
	 */
	public void setCustomerTypeID(int customerTypeID) {
		this.customerTypeID = customerTypeID;
	}

	/**
	 * Sets the primaryContactID.
	 * @param primaryContactID The primaryContactID to set
	 */
	public void setPrimaryContactID(int primaryContactID) {
		this.primaryContactID = primaryContactID;
	}

	/**
	 * Returns the timeZone.
	 * @return String
	 */
	public String getTimeZone() {
		return timeZone;
	}

	/**
	 * Sets the timeZone.
	 * @param timeZone The timeZone to set
	 */
	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}

}
