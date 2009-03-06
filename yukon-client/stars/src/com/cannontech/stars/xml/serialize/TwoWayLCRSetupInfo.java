package com.cannontech.stars.xml.serialize;

public class TwoWayLcrSetupInfo {

	private boolean isNewDevice;
	private int yukonDeviceTypeId;
	private Integer deviceId = null;
	private String deviceName = null;
	private Integer demandRate = null;
	
	public TwoWayLcrSetupInfo(boolean isNewDevice, int yukonDeviceTypeId, Integer deviceId, String deviceName, Integer demandRate) {
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
	public int getDeviceId() {
		return deviceId;
	}
	public String getDeviceName() {
		return deviceName;
	}
	public Integer getDemandRate() {
		return demandRate;
	}
}
