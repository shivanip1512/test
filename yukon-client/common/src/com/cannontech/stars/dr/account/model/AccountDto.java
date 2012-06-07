package com.cannontech.stars.dr.account.model;

import com.cannontech.common.model.Address;
import com.cannontech.common.model.SiteInformation;

public class AccountDto {

	private String accountNumber; // used to change the accountNumber on an existing account
    private String lastName;
    private String firstName;
    private String companyName;
    private String homePhone;
    private String workPhone;
    private String emailAddress;
    private String ivrLogin;
    private String voicePIN;
    private Address streetAddress = new Address();
    private Address billingAddress = new Address();
    private String userName;
    private String password;
    private String loginGroup;
    private SiteInformation siteInfo = new SiteInformation(); 
    private String mapNumber; // This is the SiteNumber column in AccountSite table
    private String altTrackingNumber;
    private Boolean isCommercial;
    private Integer commercialTypeEntryId;
    private String customerNumber;
    private Integer rateScheduleEntryId;
    private String customerStatus;
    private Boolean isCustAtHome;
    private String propertyNotes;
    private String accountNotes;

    public String getAccountNumber() {
		return accountNumber;
	}
    
    public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}
    
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getHomePhone() {
        return homePhone;
    }

    public void setHomePhone(String homePhone) {
        this.homePhone = homePhone;
    }

    public String getWorkPhone() {
        return workPhone;
    }

    public void setWorkPhone(String workPhone) {
        this.workPhone = workPhone;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getIvrLogin() {
        return ivrLogin;
    }

    public void setIvrLogin(String ivrLogin) {
        this.ivrLogin = ivrLogin;
    }

    public String getVoicePIN() {
        return voicePIN;
    }

    public void setVoicePIN(String voicePIN) {
        this.voicePIN = voicePIN;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLoginGroup() {
        return loginGroup;
    }

    public void setLoginGroup(String loginGroup) {
        this.loginGroup = loginGroup;
    }

    public Boolean getIsCommercial() {
        return isCommercial;
    }

    public void setIsCommercial(Boolean isCommercial) {
        this.isCommercial = isCommercial;
    }

    public Address getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(Address streetAddress) {
        this.streetAddress = streetAddress;
    }

    public Address getBillingAddress() {
        return billingAddress;
    }

    public void setBillingAddress(Address billingAddress) {
        this.billingAddress = billingAddress;
    }

    public SiteInformation getSiteInfo() {
        return siteInfo;
    }

    public void setSiteInfo(SiteInformation siteInfo) {
        this.siteInfo = siteInfo;
    }

    public String getMapNumber() {
        return mapNumber;
    }

    public void setMapNumber(String mapNumber) {
        this.mapNumber = mapNumber;
    }

    public String getAltTrackingNumber() {
        return altTrackingNumber;
    }

    public void setAltTrackingNumber(String altTrackingNumber) {
        this.altTrackingNumber = altTrackingNumber;
    }
    
    public Integer getCommercialTypeEntryId() {
		return commercialTypeEntryId;
	}
    
    public void setCommercialTypeEntryId(Integer commercialTypeEntryId) {
		this.commercialTypeEntryId = commercialTypeEntryId;
	}
    
    public String getCustomerNumber() {
		return customerNumber;
	}
    
    public void setCustomerNumber(String customerNumber) {
		this.customerNumber = customerNumber;
	}
    
    public Integer getRateScheduleEntryId() {
		return rateScheduleEntryId;
	}
    
    public void setRateScheduleEntryId(Integer rateScheduleEntryId) {
		this.rateScheduleEntryId = rateScheduleEntryId;
	}
    
    public String getCustomerStatus() {
		return customerStatus;
	}
    public void setCustomerStatus(String customerStatus) {
		this.customerStatus = customerStatus;
	}
    
    public Boolean getIsCustAtHome() {
		return isCustAtHome;
	}
    
    public void setIsCustAtHome(Boolean isCustAtHome) {
		this.isCustAtHome = isCustAtHome;
	}
    
    public String getAccountNotes() {
        return accountNotes;
    }
    public void setAccountNotes(String accountNotes) {
        this.accountNotes = accountNotes;
    }
    
    public String getPropertyNotes() {
        return propertyNotes;
    }
    public void setPropertyNotes(String propertyNotes) {
        this.propertyNotes = propertyNotes;
    }
}