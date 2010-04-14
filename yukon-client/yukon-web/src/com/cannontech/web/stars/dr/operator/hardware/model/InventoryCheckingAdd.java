package com.cannontech.web.stars.dr.operator.hardware.model;

import com.cannontech.common.model.Address;

public class InventoryCheckingAdd {
    private String serialNumber;
    private String name;
    private String deviceType;
    private String warehouse;
    private Address address;
    private String accountNumber;
    private String alternateTrackingNumber;
    private boolean isSwitch;
    private Integer inventoryId;

    public InventoryCheckingAdd(String serialNumber){
        this.serialNumber = serialNumber;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(String warehouse) {
        this.warehouse = warehouse;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getAltTrackingNumber() {
        return alternateTrackingNumber;
    }
    
    public void setAltTrackingNumber(String alternateTrackingNumber) {
        this.alternateTrackingNumber = alternateTrackingNumber;
    }
    
    public boolean isSwitch() {
        return isSwitch;
    }

    public void setIsSwitch(boolean isSwitch) {
        this.isSwitch = isSwitch;
    }
    
    public Integer getInventoryId() {
        return inventoryId;
    }

    public void setInventoryId(Integer inventoryId) {
        this.inventoryId = inventoryId;
    }
}