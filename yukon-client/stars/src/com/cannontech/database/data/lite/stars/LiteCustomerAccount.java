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
public class LiteCustomerAccount extends LiteBase {

	private int accountSiteID = com.cannontech.database.db.stars.customer.AccountSite.NONE_INT;
	private String accountNumber = null;
	private int customerID = com.cannontech.database.db.stars.customer.CustomerBase.NONE_INT;
	private int billingAddressID = com.cannontech.database.db.customer.CustomerAddress.NONE_INT;
	private String accountNotes = null;
	
	public LiteCustomerAccount() {
		super();
		setLiteType( LiteTypes.STARS_CUSTOMER_ACCOUNT );
	}
	
	public LiteCustomerAccount(int accountID) {
		super();
		setAccountID( accountID );
		setLiteType( LiteTypes.STARS_CUSTOMER_ACCOUNT );
	}
	
	public int getAccountID() {
		return getLiteID();
	}
	
	public void setAccountID(int accountID) {
		setLiteID( accountID );
	}

	/**
	 * Returns the accountNotes.
	 * @return String
	 */
	public String getAccountNotes() {
		return accountNotes;
	}

	/**
	 * Returns the accountNumber.
	 * @return String
	 */
	public String getAccountNumber() {
		return accountNumber;
	}

	/**
	 * Returns the accountSiteID.
	 * @return int
	 */
	public int getAccountSiteID() {
		return accountSiteID;
	}

	/**
	 * Returns the billingAddressID.
	 * @return int
	 */
	public int getBillingAddressID() {
		return billingAddressID;
	}

	/**
	 * Returns the customerID.
	 * @return int
	 */
	public int getCustomerID() {
		return customerID;
	}

	/**
	 * Sets the accountNotes.
	 * @param accountNotes The accountNotes to set
	 */
	public void setAccountNotes(String accountNotes) {
		this.accountNotes = accountNotes;
	}

	/**
	 * Sets the accountNumber.
	 * @param accountNumber The accountNumber to set
	 */
	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	/**
	 * Sets the accountSiteID.
	 * @param accountSiteID The accountSiteID to set
	 */
	public void setAccountSiteID(int accountSiteID) {
		this.accountSiteID = accountSiteID;
	}

	/**
	 * Sets the billingAddressID.
	 * @param billingAddressID The billingAddressID to set
	 */
	public void setBillingAddressID(int billingAddressID) {
		this.billingAddressID = billingAddressID;
	}

	/**
	 * Sets the customerID.
	 * @param customerID The customerID to set
	 */
	public void setCustomerID(int customerID) {
		this.customerID = customerID;
	}

}
