package com.cannontech.stars.model;

import org.apache.commons.lang3.StringUtils;

public class InventorySearch {
    
    private String serialNumber;
    private String meterNumber;
    private String accountNumber;
    private String phoneNumber;
    private String lastName;
    private String workOrderNumber;
    private String altTrackingNumber;
    
    public String getSerialNumber() {
        return serialNumber;
    }
    
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }
    
    public String getMeterNumber() {
        return meterNumber;
    }
    
    public void setMeterNumber(String meterNumber) {
        this.meterNumber = meterNumber;
    }
    
    public String getAccountNumber() {
        return accountNumber;
    }
    
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public String getWorkOrderNumber() {
        return workOrderNumber;
    }
    
    public void setWorkOrderNumber(String workOrderNumber) {
        this.workOrderNumber = workOrderNumber;
    }
    
    public String getAltTrackingNumber() {
        return altTrackingNumber;
    }
    
    public void setAltTrackingNumber(String altTrackingNumber) {
        this.altTrackingNumber = altTrackingNumber;
    }

    public void trimFields() {
        serialNumber = StringUtils.trimToNull(serialNumber);
        meterNumber = StringUtils.trimToNull(meterNumber);
        accountNumber = StringUtils.trimToNull(accountNumber);
        phoneNumber = StringUtils.trimToNull(phoneNumber);
        lastName = StringUtils.trimToNull(lastName);
        workOrderNumber = StringUtils.trimToNull(workOrderNumber);
        altTrackingNumber = StringUtils.trimToNull(altTrackingNumber);
    }
    
}