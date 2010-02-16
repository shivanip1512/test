package com.cannontech.stars.dr.general.service.impl;

import com.cannontech.common.model.Address;
import com.cannontech.database.data.lite.LiteContactNotification;

public class AccountSearchResult {

	private int accountId;
	private int energyCompanyId;
	private String accountNumber;
	private String name;
	private LiteContactNotification homePhoneNotif ;
	private LiteContactNotification workPhoneNotif;
	private Address address;
	
	public AccountSearchResult(int accountId, int energyCompanyId, String accountNumber, String name, LiteContactNotification homePhoneNotif, LiteContactNotification workPhoneNotif, Address address) {
		
		this.accountId = accountId;
		this.energyCompanyId = energyCompanyId;
		this.accountNumber = accountNumber;
		this.name = name;
		this.homePhoneNotif = homePhoneNotif;
		this.workPhoneNotif = workPhoneNotif;
		this.address = address;
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
	
	public String getName() {
		return name;
	}
	
	public LiteContactNotification getHomePhoneNotif() {
		return homePhoneNotif;
	}
	
	public LiteContactNotification getWorkPhoneNotif() {
		return workPhoneNotif;
	}
	
	public Address getAddress() {
		return address;
	}
}
