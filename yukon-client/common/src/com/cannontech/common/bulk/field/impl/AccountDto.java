package com.cannontech.common.bulk.field.impl;

import com.cannontech.common.model.Address;
import com.cannontech.database.data.lite.LiteEnergyCompany;

public class AccountDto {

    private String accountNumber;
    private LiteEnergyCompany liteEnergyCompany;
    private String lastName;
    private String firstName;
    private String companyName;
    private String accountType;
    private String homePhone;
    private String workPhone;
    private String emailAddress;
    private Address streetAddress;
    private Address billingAddress;
    private String customerType;
    private String userName;
    private String password;
    private String loginGroup;

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public LiteEnergyCompany getLiteEnergyCompany() {
        return liteEnergyCompany;

    }

    public void setLiteEnergyCompany(LiteEnergyCompany liteEnergyCompany) {
        this.liteEnergyCompany = liteEnergyCompany;
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

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
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

}
