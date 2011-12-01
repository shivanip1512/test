package com.cannontech.stars;

import com.cannontech.common.pao.PaoType;
import com.cannontech.stars.model.LiteLmHardware;

public class InventorySearchResult extends LiteLmHardware{

    private int deviceId;
    private String accountNumber;
    private String phoneNumber;
    private String lastName;
    private String workOrderNumber;
    private String altTrackingNumber;
    private Integer addressId;
    private String energyCompanyName;
    private PaoType paoType;
    
    public int getDeviceId() {
        return deviceId;
    }
    
    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
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
    
    public Integer getAddressId() {
        return addressId;
    }
    
    public void setAddressId(Integer addressId) {
        this.addressId = addressId;
    }
    
    public String getEnergyCompanyName() {
        return energyCompanyName;
    }
    
    public void setEnergyCompanyName(String energyCompanyName) {
        this.energyCompanyName = energyCompanyName;
    }
    
    public PaoType getPaoType() {
        return paoType;
    }
    
    public void setPaoType(PaoType paoType) {
        this.paoType = paoType;
    }

}