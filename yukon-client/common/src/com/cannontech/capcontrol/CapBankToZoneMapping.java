package com.cannontech.capcontrol;

public class CapBankToZoneMapping {
	private Integer deviceId;
	private Integer zoneId;
	private double graphPositionOffset;
	private double distance;
	
	public CapBankToZoneMapping() {
		
	}
	
	public CapBankToZoneMapping(Integer deviceId, Integer zoneId, double graphPositionOffset, double distance) {
		this.deviceId = deviceId;
		this.zoneId = zoneId;
		this.graphPositionOffset = graphPositionOffset;
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
