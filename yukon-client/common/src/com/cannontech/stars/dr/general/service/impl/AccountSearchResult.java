package com.cannontech.stars.dr.general.service.impl;

import com.cannontech.common.model.Address;
import com.cannontech.database.data.lite.LiteContactNotification;

public class AccountSearchResult {

	private int accountId;
	private int energyCompanyId;
	private String accountNumber;
	private String altTrackingNumber;
	private String firstName;
	private String lastName;
	private String companyName;
	private LiteContactNotification homePhoneNotif ;
	private LiteContactNotification workPhoneNotif;
	private Address address;
	private String energyCompanyName;
	
	public AccountSearchResult(int accountId, int energyCompanyId,
			                   String accountNumber, String altTrackingNumber,
			                   String firstName, String lastName, String companyName,
							   LiteContactNotification homePhoneNotif, LiteContactNotification workPhoneNotif, 
							   Address address, String energyCompanyName) {
		
		this.accountId = accountId;
		this.energyCompanyId = energyCompanyId;
		this.accountNumber = accountNumber;
		this.altTrackingNumber = altTrackingNumber;
		this.firstName = firstName;
		this.lastName = lastName;
		this.companyName = companyName;
		this.homePhoneNotif = homePhoneNotif;
		this.workPhoneNotif = workPhoneNotif;
		this.address = address;
		this.energyCompanyName = energyCompanyName;
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
	
	public String getAltTrackingNumber() {
		return altTrackingNumber;
	}
	
	public String getFirstName() {
		return firstName;
	}
	
	public String getLastName() {
		return lastName;
	}
	
	public String getCompanyName() {
		return companyName;
	}
	
	public String getCombinedName() {
		
		// name
		String name = "";
    	name += lastName + ", ";
    	name += firstName;
    	if (companyName !=  null) {
    		name += "(" + companyName + ")";
    	}
    	
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

    public String getEnergyCompanyName() {
        return energyCompanyName;
    }

    public void setEnergyCompanyName(String energyCompanyName) {
        this.energyCompanyName = energyCompanyName;
    }

}