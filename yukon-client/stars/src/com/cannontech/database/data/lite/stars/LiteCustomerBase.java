package com.cannontech.database.data.lite.stars;

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
public class LiteCustomerBase extends LiteBase {
	
	private int primaryContactID = 0;
	private int customerTypeID = com.cannontech.database.db.stars.CustomerListEntry.NONE_INT;
	private java.util.ArrayList additionalContacts = null;

	public LiteCustomerBase() {
		super();
		setLiteType( LiteTypes.STARS_CUSTOMER_BASE );
	}
	
	public LiteCustomerBase(int customerID) {
		super();
		setCustomerID( customerID );
		setLiteType( LiteTypes.STARS_CUSTOMER_BASE );
	}
	
	public int getCustomerID() {
		return getLiteID();
	}
	
	public void setCustomerID(int customerID) {
		setLiteID( customerID );
	}
	/**
	 * Returns the additionalContacts.
	 * @return java.util.ArrayList
	 */
	public java.util.ArrayList getAdditionalContacts() {
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

}
