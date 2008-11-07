package com.cannontech.amr.account.model;

import com.cannontech.common.model.Address;

public class AccountInfo {
    private int accountId;
    private String firstName;
    private String lastName;
    private Address address;

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(final int accountId) {
        this.accountId = accountId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(final String lastName) {
        this.lastName = lastName;
    }


    public Address getAddress() {
        return address;
    }

    public void setAddress(final Address address) {
        this.address = address;
    }
    
    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof AccountInfo)) return false;
        AccountInfo obj = (AccountInfo) o;
        return ((this.accountId == obj.accountId) &&
                (this.address.equals(obj.address)) &&
                (this.firstName.equals(obj.firstName)) &&
                (this.lastName.equals(obj.lastName)));
    }
    
    @Override
    public int hashCode() {
        int result = 17;
        result = result * 37 + this.accountId;
        result = result * 37 + this.firstName.hashCode();
        result = result * 37 + this.lastName.hashCode();
        return result;
    }
    
    @Override
    public String toString() {
        return AccountInfo.class.getName() + ": " +
            this.accountId + " " +
            this.firstName + " " +
            this.lastName;
    }

}