package com.cannontech.stars.model;

import com.cannontech.common.inventory.InventoryIdentifier;
import com.cannontech.common.inventory.YukonInventory;

public class LiteLmHardware implements YukonInventory {

    private InventoryIdentifier identifier;
    private int deviceId; // zero if not a 'YukonPAObject'
    private String serialNumber;
    private String label;
    private int accountId;
    private int energyCompanyId;
    private String accountNo;
    
    public InventoryIdentifier getIdentifier() {
        return identifier;
    }
    
    public void setIdentifier(InventoryIdentifier identifier) {
        this.identifier = identifier;
    }
    
    public int getDeviceId() {
        return deviceId;
    }
    
    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }
    
    public String getSerialNumber() {
        return serialNumber;
    }
    
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }
    
    public String getLabel() {
        return label;
    }
    
    public void setLabel(String label) {
        this.label = label;
    }
    
    public int getAccountId() {
        return accountId;
    }
    
    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }
    
    public int getEnergyCompanyId() {
        return energyCompanyId;
    }
    
    public void setEnergyCompanyId(int energyCompanyId) {
        this.energyCompanyId = energyCompanyId;
    }

    @Override
    public InventoryIdentifier getInventoryIdentifier() {
        return identifier;
    }

    public String getAccountNo() {
        return accountNo;
    }
    
    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }
    
}