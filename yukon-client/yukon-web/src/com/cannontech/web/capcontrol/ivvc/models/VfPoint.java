package com.cannontech.web.capcontrol.ivvc.models;

import com.cannontech.common.model.Phase;

public class VfPoint {
    
	private String description;
	private String zoneName;
	private Phase phase;
	private boolean regulator;
    private Double x;
    private Double y;
    private Integer seriesId;
    private boolean ignore;
    
    public VfPoint(Double x, Double y, Integer seriesId) {
        this(null, null, null, false, x, y, seriesId, false);
    }
    
    public VfPoint(String description, String zoneName, Phase phase, boolean regulator, Double x, Double y, boolean ignore) {
        this(description, zoneName, phase, regulator, x, y, null, ignore);
    }
    
    public VfPoint(String description, String zoneName, Phase phase, boolean regulator, Double x, Double y, Integer seriesId, boolean ignore) {
        this.description = description;
        this.zoneName = zoneName;
        this.phase = phase;
        this.regulator = regulator;
        this.x = x;
        this.y = y;
        this.seriesId = seriesId;
        this.ignore = ignore;
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

    public String getZoneName() {
        return zoneName;
    }

    public void setZoneName(String zoneName) {
        this.zoneName = zoneName;
    }

    public boolean isIgnore() {
        return ignore;
    }

    public void setIgnore(boolean ignore) {
        this.ignore = ignore;
    }

}
