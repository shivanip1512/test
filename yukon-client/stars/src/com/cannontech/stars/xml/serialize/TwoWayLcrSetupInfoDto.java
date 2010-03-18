package com.cannontech.stars.xml.serialize;

public class TwoWayLcrSetupInfoDto {

	private boolean isNewDevice;
	private int yukonDeviceTypeId;
	private Integer deviceId = null;
	private String deviceName = null;
	private Integer demandRate = 300; // Default to 5 min
	
	public TwoWayLcrSetupInfoDto(boolean isNewDevice, int yukonDeviceTypeId, Integer deviceId, String deviceName, Integer demandRate) {
		this.isNewDevice = isNewDevice;
		this.yukonDeviceTypeId = yukonDeviceTypeId;
		this.deviceId = deviceId;
		this.deviceName = deviceName;
		this.demandRate = demandRate;
	}
	
	public boolean isNewDevice() {
		return isNewDevice;
	}
	public int getYukonDeviceTypeId() {
		return yukonDeviceTypeId;
	}
	public Integer getDeviceId() {
		return deviceId;
	}
	public String getDeviceName() {
		return deviceName;
	}
	public Integer getDemandRate() {
		return demandRate;
	}
}
