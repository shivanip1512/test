package com.cannontech.capcontrol.model;

public class ZoneAssignmentCapBankRow {
    
    private String type;
    private int id;
    private String name;
    private String device;
    private double graphPositionOffset;
    private double distance;
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDevice() {
        return device;
    }
    
    public void setDevice(String device) {
        this.device = device;
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
