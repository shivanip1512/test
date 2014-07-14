package com.cannontech.web.capcontrol.ivvc.models;

import java.util.List;

import com.cannontech.common.model.Phase;

public class VfLine {
    
    private int id;
    private String lineName;
    private String zoneName;
    private Phase phase;
    private VfLineSettings settings;
    private List<VfPoint> points;
    
    /**
     * Only here for unit tests
     */
    public VfLine() {
    }
    
    public VfLine(int id, Phase phase, VfLineSettings settings, List<VfPoint> points) {
        this(id, null, null, phase, settings, points);
    }
    
    public VfLine(int id, String lineName, String zoneName, Phase phase, VfLineSettings settings, List<VfPoint> points) {
        this.id = id;
        this.lineName = lineName;
        this.zoneName = zoneName;
        this.phase = phase;
        this.settings = settings;
        this.points = points;
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public List<VfPoint> getPoints() {
        return points;
    }
    
    public void setPoints(List<VfPoint> points) {
        this.points = points;
    }

	public String getLineName() {
		return lineName;
	}

	public void setLineName(String lineName) {
		this.lineName = lineName;
	}

	public String getZoneName() {
        return zoneName;
    }

    public void setZoneName(String zoneName) {
        this.zoneName = zoneName;
    }

    public VfLineSettings getSettings() {
		return settings;
	}

	public void setSettings(VfLineSettings settings) {
		this.settings = settings;
	}

    public Phase getPhase() {
        return phase;
    }

    public void setPhase(Phase phase) {
        this.phase = phase;
    }
}
