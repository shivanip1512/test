package com.cannontech.capcontrol.model;

public abstract class ZoneAssignmentRow {
    private int id;
    private String name;
    private String device;
    private double graphPositionOffset;
    private double distance;
    private boolean isDeletion = false;
    
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

    public boolean isDeletion() {
        return isDeletion;
    }

    public void setDeletion(boolean isDeletion) {
        this.isDeletion = isDeletion;
    }
}
