package com.cannontech.web.capcontrol.ivvc.models;

import com.cannontech.enums.Phase;

public class VfPoint {
    
	private String description;
	private Phase phase;
	private boolean regulator;
    private Double x;
    private Double y;
    private Integer seriesId;
    
    public VfPoint() {
    }
    
    public VfPoint(Double x, Double y, Integer seriesId) {
        this(null, null, false, x, y, seriesId);
    }

    public VfPoint(Double x, Double y) {
        this(null, null, false, x, y, null);
    }
    
    public VfPoint(VfPoint point) {
        this(point.getDescription(), point.getPhase(), point.isRegulator(), point.getX(), point.getY(), point.getSeriesId());
    }
    
    public VfPoint(String description, Phase phase, boolean regulator, Double x, Double y, Integer seriesId) {
        this.description = description;
        this.phase = phase;
        this.regulator = regulator;
    	this.x = x;
        this.y = y;
        this.seriesId = seriesId;
    }
    
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Phase getPhase() {
        return phase;
    }

    public void setPhase(Phase phase) {
        this.phase = phase;
    }

    public boolean isRegulator() {
        return regulator;
    }

    public void setRegulator(boolean regulator) {
        this.regulator = regulator;
    }

    public Double getX() {
        return x;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public Double getY() {
        return y;
    }

    public void setY(Double y) {
        this.y = y;
    }

    public Integer getSeriesId() {
        return seriesId;
    }

    public void setSeriesId(Integer seriesId) {
        this.seriesId = seriesId;
    }
}
