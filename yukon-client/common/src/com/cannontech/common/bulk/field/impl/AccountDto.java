package com.cannontech.common.bulk.field.impl;

import com.cannontech.common.model.Address;
import com.cannontech.common.model.SiteInformation;

public class AccountDto {

    private String lastName;
    private String firstName;
    private String companyName;
    private String homePhone;
    private String workPhone;
    private String emailAddress;
    private Address streetAddress = new Address();
    private Address billingAddress = new Address();
    private String customerType;
    private String userName;
    private String password;
    private String loginGroup;
    private SiteInformation siteInfo = new SiteInformation(); 
    private String mapNumber; // translates to SiteNumber in AccountSite
    private String altTrackingNumber;

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

    public String getCustomerType() {
        return customerType;
    }

    public void setCustomerType(String customerType) {
        this.customerType = customerType;
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

}
