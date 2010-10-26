package com.cannontech.capcontrol;

public class PointToZoneMapping {
	private Integer pointId;
	private Integer zoneId;
	private double zoneOrder;
	
	public PointToZoneMapping() {
		
	}
	
	public PointToZoneMapping(Integer pointId, Integer zoneId, double zoneOrder) {
		this.pointId = pointId;
		this.zoneId = zoneId;
		this.zoneOrder = zoneOrder;
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

	public double getZoneOrder() {
		return zoneOrder;
	}

	public void setZoneOrder(double zoneOrder) {
		this.zoneOrder = zoneOrder;
	}
}
