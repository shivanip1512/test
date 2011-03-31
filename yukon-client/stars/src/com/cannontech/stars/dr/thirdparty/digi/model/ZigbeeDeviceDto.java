package com.cannontech.stars.dr.thirdparty.digi.model;

import com.cannontech.common.inventory.InventoryIdentifier;
import com.cannontech.common.inventory.YukonInventory;

public class ZigbeeDeviceDto implements YukonInventory {
	private int deviceId;
	private String serialNumber;
	private String deviceType;
	private int connectionStatusId;
	private int commissionId;
	private InventoryIdentifier inventoryIdentifier;
	private Integer gatewayId;
	
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
	
	public String getDeviceType() {
		return deviceType;
	}
	
	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}
	
	public int getConnectionStatusId() {
		return connectionStatusId;
	}
	
	public void setConnectionStatusId(int connectionStatusId) {
		this.connectionStatusId = connectionStatusId;
	}
	
	public int getCommissionId() {
		return commissionId;
	}
	
	public void setCommissionId(int commissionId) {
		this.commissionId = commissionId;
	}
    
	public void setInventoryIdentifier(InventoryIdentifier inventoryIdentifier) {
        this.inventoryIdentifier = inventoryIdentifier;
    }
    
    @Override
    public InventoryIdentifier getInventoryIdentifier() {
        return inventoryIdentifier;
    }

    public void setGatewayId(Integer gatewayId) {
        this.gatewayId = gatewayId;
    }

    public Integer getGatewayId() {
        return gatewayId;
    }

}