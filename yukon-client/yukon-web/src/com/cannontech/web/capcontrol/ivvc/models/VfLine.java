package com.cannontech.web.capcontrol.ivvc.models;

import java.util.List;

public class VfLine {
    
    private int id;
    private String zoneName;
    private VfLineSettings settings;
    private List<VfPoint> points;
    
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
}
