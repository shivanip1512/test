package com.cannontech.capcontrol;

public class CapBankToZoneMapping {
	private Integer deviceId;
	private Integer zoneId;
	private double zoneOrder;
	
	public CapBankToZoneMapping() {
		
	}
	
	public CapBankToZoneMapping(Integer deviceId, Integer zoneId, double zoneOrder) {
		this.deviceId = deviceId;
		this.zoneId = zoneId;
		this.zoneOrder = zoneOrder;
	}
	
	public Integer getDeviceId() {
		return deviceId;
	}
	
	public void setDeviceId(Integer deviceId) {
		this.deviceId = deviceId;
	}
	
	public Integer getZoneId() {
		return zoneId;
	}

	public void setZoneId(Integer zoneId) {
		this.zoneId = zoneId;
	}

	public double getZoneOrder() {
		return zoneOrder;
	}

	public void setZoneOrder(double zoneOrder) {
		this.zoneOrder = zoneOrder;
	}
}
