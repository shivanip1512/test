package com.cannontech.web.stars.dr.operator.general;

import com.cannontech.common.model.Address;
import com.cannontech.database.data.lite.LiteCICustomer;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteContactNotification;
import com.cannontech.database.data.lite.LiteCustomer;
import com.cannontech.stars.dr.account.model.CustomerAccount;

public class AccountInfoFragment {

	private int accountId;
	private int energyCompanyId;
	private CustomerAccount customerAccount;
	private LiteCustomer customer;
	private LiteContact primaryContact;
	private Address address;
	private LiteContactNotification homePhoneNotif;
	private LiteContactNotification workPhoneNotif;
	
	public AccountInfoFragment(int accountId, int energyCompanyId) {
		this.accountId = accountId;
		this.energyCompanyId = energyCompanyId;
	}
	
	// SETTERS
	public void setCustomerAccount(CustomerAccount customerAccount) {
		this.customerAccount = customerAccount;
	}
	public void setCustomer(LiteCustomer customer) {
		this.customer = customer;
	}
	public void setPrimaryContact(LiteContact primaryContact) {
		this.primaryContact = primaryContact;
	}
	public void setAddress(Address address) {
		this.address = address;
	}
	public void setHomePhoneNotif(LiteContactNotification homePhoneNotif) {
		this.homePhoneNotif = homePhoneNotif;
	}
	public void setWorkPhoneNotif(LiteContactNotification workPhoneNotif) {
		this.workPhoneNotif = workPhoneNotif;
	}
	
	// PRIMARY GETTERS
	public int getAccountId() {
		return accountId;
	}
	public int getEnergyCompanyId() {
		return energyCompanyId;
	}
	public String getAccountNumber() {
		return customerAccount.getAccountNumber();
	}
	public String getCompanyName() {
		if(customer instanceof LiteCICustomer) {
    		return ((LiteCICustomer) customer).getCompanyName();
        }
		return null;
	}
	public String getFirstName() {
		return primaryContact.getContFirstName();
	}
	public String getLastName() {
		return primaryContact.getContLastName();
	}
	public Address getAddress() {
		return address;
	}
	public LiteContactNotification getHomePhoneNotif() {
		return homePhoneNotif;
	}
	public LiteContactNotification getWorkPhoneNotif() {
		return workPhoneNotif;
	}
	
	// EXTRA GETTERS
	public String getAlternateTrackingNumber() {
		return customer.getAltTrackingNumber();
	}
	public String getCustomerAccountNotes() {
		return customerAccount.getAccountNotes();
	}
}
