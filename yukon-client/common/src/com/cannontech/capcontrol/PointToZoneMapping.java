package com.cannontech.capcontrol;

public class PointToZoneMapping {
	private int pointId;
	private int zoneId;
	private double graphPositionOffset;
	private double distance;
	
	public PointToZoneMapping() {
		
	}
	
	public PointToZoneMapping(int pointId, int zoneId, double graphPositionOffset, double distance) {
		this.pointId = pointId;
		this.zoneId = zoneId;
		this.graphPositionOffset = graphPositionOffset;
		this.distance = distance;
	}
	
	public int getPointId() {
		return pointId;
	}

	public void setPointId(int pointId) {
		this.pointId = pointId;
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
