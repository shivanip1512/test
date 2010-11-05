package com.cannontech.capcontrol;

public class CapBankToZoneMapping {
	private int deviceId;
	private int zoneId;
	private double graphPositionOffset;
	private double distance;
	
	public CapBankToZoneMapping() {
		
	}
	
	public CapBankToZoneMapping(int deviceId, int zoneId, double graphPositionOffset, double distance) {
		this.deviceId = deviceId;
		this.zoneId = zoneId;
		this.graphPositionOffset = graphPositionOffset;
		this.distance = distance;
	}
	
	public int getDeviceId() {
		return deviceId;
	}
	
	public void setDeviceId(int deviceId) {
		this.deviceId = deviceId;
	}
	
	public int getZoneId() {
		return zoneId;
	}

	public void setZoneId(int zoneId) {
		this.zoneId = zoneId;
	}

	public double getGraphPositionOffset() {
		return graphPositionOffset;
	}

	public void setGraphPositionOffset(double graphPositionOffset) {
		this.graphPositionOffset = graphPositionOffset;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}
}
