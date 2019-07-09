package com.cannontech.stars.dr.hardware.model;

import org.apache.commons.lang3.StringUtils;

import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.inventory.InventoryIdentifier;

public class HardwareSummary {

    private InventoryIdentifier inventoryIdentifier;
    private String deviceLabel;
    private String serialNumber;
    private int deviceId;

    public HardwareSummary(InventoryIdentifier inventoryIdentifier, String deviceLabel, String serialNumber, int deviceId) {
        this.setInventoryIdentifier(inventoryIdentifier);
        this.deviceLabel = deviceLabel;
        this.serialNumber = serialNumber;
        this.setDeviceId(deviceId);
    }

    public String getDeviceLabel() {
        return deviceLabel;
    }
    
    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
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
        return getInventoryIdentifier().getHardwareType().getHardwareConfigType().getNumRelays();
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

    public int getDeviceId() {
        return deviceId;
    }

    
    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((deviceLabel == null) ? 0 : deviceLabel.hashCode());
		result = prime
				* result
				+ ((inventoryIdentifier == null) ? 0 : inventoryIdentifier
						.hashCode());
		result = prime * result
				+ ((serialNumber == null) ? 0 : serialNumber.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		HardwareSummary other = (HardwareSummary) obj;
		if (deviceLabel == null) {
			if (other.deviceLabel != null)
				return false;
		} else if (!deviceLabel.equals(other.deviceLabel))
			return false;
		if (inventoryIdentifier == null) {
			if (other.inventoryIdentifier != null)
				return false;
		} else if (!inventoryIdentifier.equals(other.inventoryIdentifier))
			return false;
		if (serialNumber == null) {
			if (other.serialNumber != null)
				return false;
		} else if (!serialNumber.equals(other.serialNumber))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String.format("HardwareSummary [inventoryIdentifier=%s, deviceLabel=%s, serialNumber=%s]", inventoryIdentifier, deviceLabel, serialNumber);
	}
}