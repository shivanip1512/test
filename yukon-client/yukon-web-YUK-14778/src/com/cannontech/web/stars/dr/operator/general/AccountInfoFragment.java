package com.cannontech.web.stars.dr.operator.general;

import com.cannontech.common.model.Address;
import com.cannontech.database.data.lite.LiteContactNotification;
import com.cannontech.stars.dr.general.service.impl.AccountSearchResult;

public class AccountInfoFragment {

	private int accountId;
	private int energyCompanyId;
	private String accountNumber;
	private String alternateTrackingNumber;
	private String companyName;
	private String firstName;
	private String lastName;
	private Address address;
	private LiteContactNotification homePhoneNotif;
	private LiteContactNotification workPhoneNotif;
	
	public AccountInfoFragment(int accountId, int energyCompanyId) {
		this.accountId = accountId;
		this.energyCompanyId = energyCompanyId;
	}
	
	public AccountInfoFragment(AccountSearchResult accountSearchResult) {
		
		this.accountId = accountSearchResult.getAccountId();
		this.energyCompanyId = accountSearchResult.getEnergyCompanyId();
		this.accountNumber = accountSearchResult.getAccountNumber();
		this.alternateTrackingNumber = accountSearchResult.getAltTrackingNumber();
		this.firstName = accountSearchResult.getFirstName();
		this.lastName = accountSearchResult.getLastName();
		this.companyName = accountSearchResult.getCompanyName();
		this.address = accountSearchResult.getAddress();
		this.homePhoneNotif = accountSearchResult.getHomePhoneNotif();
		this.workPhoneNotif = accountSearchResult.getWorkPhoneNotif();
	}
	
	// SETTERS
	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
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
	public void setAlternateTrackingNumber(String alternateTrackingNumber) {
		this.alternateTrackingNumber = alternateTrackingNumber;
	}
	
	// PRIMARY GETTERS
	public int getAccountId() {
		return accountId;
	}
	public int getEnergyCompanyId() {
		return energyCompanyId;
	}
	public String getAccountNumber() {
		return accountNumber;
	}
	public String getAlternateTrackingNumber() {
		return alternateTrackingNumber;
	}
	public String getCompanyName() {
		return companyName;
	}
	public String getFirstName() {
		return firstName;
	}
	public String getLastName() {
		return lastName;
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
}
