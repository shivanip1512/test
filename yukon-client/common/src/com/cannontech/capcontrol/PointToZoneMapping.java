package com.cannontech.capcontrol;

public class PointToZoneMapping {
	private Integer pointId;
	private Integer zoneId;
	private double graphPositionOffset;
	private double distance;
	
	public PointToZoneMapping() {
		
	}
	
	public PointToZoneMapping(Integer pointId, Integer zoneId, double graphPositionOffset, double distance) {
		this.pointId = pointId;
		this.zoneId = zoneId;
		this.graphPositionOffset = graphPositionOffset;
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
