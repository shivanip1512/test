package com.cannontech.capcontrol;

public class CapBankToZoneMapping {
	private Integer deviceId;
	private Integer zoneId;
	private double position;
	private double distance;
	
	public CapBankToZoneMapping() {
		
	}
	
	public CapBankToZoneMapping(Integer deviceId, Integer zoneId, double position, double distance) {
		this.deviceId = deviceId;
		this.zoneId = zoneId;
		this.position = position;
		this.distance = distance;
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

	public double getPosition() {
		return position;
	}

	public void setPosition(double position) {
		this.position = position;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}
}
