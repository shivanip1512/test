package com.cannontech.capcontrol;

public class PointToZoneMapping {
	private Integer pointId;
	private Integer zoneId;
	private double position;
	private double distance;
	
	public PointToZoneMapping() {
		
	}
	
	public PointToZoneMapping(Integer pointId, Integer zoneId, double position, double distance) {
		this.pointId = pointId;
		this.zoneId = zoneId;
		this.position = position;
		this.distance = distance;
	}
	
	public Integer getPointId() {
		return pointId;
	}

	public void setPointId(Integer pointId) {
		this.pointId = pointId;
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
