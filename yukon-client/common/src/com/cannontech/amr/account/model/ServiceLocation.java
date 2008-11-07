package com.cannontech.amr.account.model;

import com.cannontech.common.model.Address;


public class ServiceLocation {
    private String customerNumber;
    private String accountNumber;
    private String siteNumber;
    private Address address;
    private String mapLocation;
    
    public ServiceLocation() {
        
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(final String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(final Address address) {
        this.address = address;
    }

    public String getCustomerNumber() {
        return customerNumber;
    }

    public void setCustomerNumber(final String customerNumber) {
        this.customerNumber = customerNumber;
    }

    public String getSiteNumber() {
        return siteNumber;
    }

    public void setSiteNumber(final String siteNumber) {
        this.siteNumber = siteNumber;
    }
    
    public String getMapLocation() {
        return mapLocation;
    }

    public void setMapLocation(final String mapLocation) {
        this.mapLocation = mapLocation;
    }
    
    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof ServiceLocation)) return false;
        ServiceLocation obj = (ServiceLocation) o;
        return ((this.accountNumber.equals(obj.accountNumber) &&
                (this.address.equals(obj.address)) &&
                (this.customerNumber.equals(obj.customerNumber)) &&
                (this.mapLocation.equals(obj.mapLocation)) &&
                (this.siteNumber.equals(obj.siteNumber))));
    }
    
    @Override
    public int hashCode() {
        int result = 17;
        result = result * 37 + this.accountNumber.hashCode();
        result = result * 37 + this.customerNumber.hashCode();
        result = result * 37 + this.mapLocation.hashCode();
        result = result * 37 + this.siteNumber.hashCode();
        return result;
    }
    
    @Override
    public String toString() {
        return ServiceLocation.class.getName() + ": " +
            this.accountNumber + " " +
            this.customerNumber;
    }
    
}
