package com.cannontech.stars.dr.thirdparty.digi.model;

public class ZigbeeDeviceDto {
	private int deviceId;
	private String serialNumber;
	private String deviceType;
	private int connectionStatusId;
	private int commissionId;
	
	
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
}
