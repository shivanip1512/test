package com.cannontech.stars.xml.serialize;

public class TwoWayLCRSetupInfo {

	private boolean isNewDevice;
	private Integer deviceId = null;
	private String deviceName = null;
	private Integer demandRate = null;
	
	public TwoWayLCRSetupInfo(boolean isNewDevice, Integer deviceId, String deviceName, Integer demandRate) {
		this.isNewDevice = isNewDevice;
		this.deviceId = deviceId;
		this.deviceName = deviceName;
		this.demandRate = demandRate;
	}
	
	public boolean isNewDevice() {
		return isNewDevice;
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
