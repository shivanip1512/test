package com.cannontech.capcontrol;

import com.cannontech.common.model.Phase;

public class PointToZoneMapping {
	private int pointId;
	private int zoneId;
	private double graphPositionOffset;
	private double distance;
	private Phase phase;
	private boolean ignore;
	
	public PointToZoneMapping() {
		
	}
	
	public PointToZoneMapping(int pointId, int zoneId, double graphPositionOffset, double distance, Phase phase, boolean ignore) {
		this.pointId = pointId;
		this.zoneId = zoneId;
		this.graphPositionOffset = graphPositionOffset;
		this.distance = distance;
		this.phase = phase;
		this.ignore = ignore;
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

    public Phase getPhase() {
        return phase;
    }

    public void setPhase(Phase phase) {
        this.phase = phase;
    }

    public boolean isIgnore() {
        return ignore;
    }

    public void setIgnore(boolean ignore) {
        this.ignore = ignore;
    }
}
