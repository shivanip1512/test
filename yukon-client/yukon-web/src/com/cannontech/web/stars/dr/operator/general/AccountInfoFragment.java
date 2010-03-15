package com.cannontech.web.stars.dr.operator.general;

import com.cannontech.common.model.Address;
import com.cannontech.database.data.lite.LiteContactNotification;

public class AccountInfoFragment {

	private int accountId;
	private int energyCompanyId;
	private String accountNumber;
	private String companyName;
	private String firstName;
	private String lastName;
	private Address address;
	private LiteContactNotification homePhoneNotif;
	private LiteContactNotification workPhoneNotif;
	
	public AccountInfoFragment(int accountId, int energyCompanyId, String accountNumber) {
		this.accountId = accountId;
		this.energyCompanyId = energyCompanyId;
		this.accountNumber = accountNumber;
	}
	
	public int getAccountId() {
		return accountId;
	}
	public int getEnergyCompanyId() {
		return energyCompanyId;
	}
	public String getAccountNumber() {
		return accountNumber;
	}
	
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public Address getAddress() {
		return address;
	}
	public void setAddress(Address address) {
		this.address = address;
	}
	public LiteContactNotification getHomePhoneNotif() {
		return homePhoneNotif;
	}
	public void setHomePhoneNotif(LiteContactNotification homePhoneNotif) {
		this.homePhoneNotif = homePhoneNotif;
	}
	public LiteContactNotification getWorkPhoneNotif() {
		return workPhoneNotif;
	}
	public void setWorkPhoneNotif(LiteContactNotification workPhoneNotif) {
		this.workPhoneNotif = workPhoneNotif;
	}
}
