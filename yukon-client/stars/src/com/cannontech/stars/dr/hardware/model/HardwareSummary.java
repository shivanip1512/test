package com.cannontech.stars.dr.hardware.model;

import org.apache.commons.lang.StringUtils;

import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.inventory.InventoryIdentifier;

public class HardwareSummary {

    private InventoryIdentifier inventoryIdentifier;
    private String deviceLabel;
    private String serialNumber;

    public HardwareSummary(InventoryIdentifier inventoryIdentifier, String deviceLabel, String serialNumber, HardwareType hardwareType) {
        this.setInventoryIdentifier(inventoryIdentifier);
        this.deviceLabel = deviceLabel;
        this.serialNumber = serialNumber;
    }

    public String getDeviceLabel() {
        return deviceLabel;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public String getDisplayName() {
        if (StringUtils.isBlank(deviceLabel)) {
            return serialNumber;
        }

        return deviceLabel;
    }


    public int getNumRelays() {
        return getInventoryIdentifier().getHardwareType().getNumRelays();
    }

    public void setInventoryIdentifier(InventoryIdentifier inventoryIdentifier) {
        this.inventoryIdentifier = inventoryIdentifier;
    }

    public InventoryIdentifier getInventoryIdentifier() {
        return inventoryIdentifier;
    }
    
    public HardwareType getHardwareType() {
        return inventoryIdentifier.getHardwareType();
    }
    
    public int getInventoryId() {
        return inventoryIdentifier.getInventoryId();
    }
    
}