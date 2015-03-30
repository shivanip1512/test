package com.cannontech.stars.model;

import com.cannontech.common.inventory.InventoryIdentifier;
import com.cannontech.common.inventory.YukonInventory;
import com.cannontech.common.pao.PaoIdentifier;

public class AssetReportDevice implements YukonInventory {
    
    private InventoryIdentifier inventoryIdentifier;
    private PaoIdentifier paoIdentifier;
    private int deviceId; // zero if this is NOT a 'YukonPAObject'
    private String name; // null if this is NOT a 'YukonPAObject'
    private String meterNumber; // null if this is NOT a meter
    private String serialNumber; // null if this is a meter
    private String label;
    private int accountId;
    private String accountNo;
    private int energyCompanyId;
    
    @Override
    public InventoryIdentifier getInventoryIdentifier() {
        return inventoryIdentifier;
    }
    
    public void setInventoryIdentifier(InventoryIdentifier inventoryIdentifier) {
        this.inventoryIdentifier = inventoryIdentifier;
    }
    
    public PaoIdentifier getPaoIdentifier() {
        return paoIdentifier;
    }
    
    public void setPaoIdentifier(PaoIdentifier paoIdentifier) {
        this.paoIdentifier = paoIdentifier;
    }
    
    /** Device id will be zero if this is NOT a 'YukonPAOjbect'. */
    public int getDeviceId() {
        return deviceId;
    }
    
    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }
    
    /** Name will be null if this is NOT a 'YukonPAOjbect'. */
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    /** Meter number will be null if this is NOT a meter. */
    public String getMeterNumber() {
        return meterNumber;
    }
    
    public void setMeterNumber(String meterNumber) {
        this.meterNumber = meterNumber;
    }
    
    /** Serial number will be null if this is a meter. */
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
    
    public String getAccountNo() {
        return accountNo;
    }
    
    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }
    
    @Override
    public String toString() {
        return String.format("AssetsReportDevice [inventoryIdentifier=%s, deviceId=%s, name=%s, meterNumber=%s, " 
                + "serialNumber=%s, label=%s, accountId=%s, accountNo=%s, energyCompanyId=%s]",
                inventoryIdentifier, deviceId, name, meterNumber, serialNumber, label, accountId, accountNo, 
                energyCompanyId);
    }
    
}