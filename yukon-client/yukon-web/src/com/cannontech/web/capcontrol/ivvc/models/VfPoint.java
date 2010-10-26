package com.cannontech.web.capcontrol.ivvc.models;

public class VfPoint {
    
	private String description;
    private double x;
    private double y;
    
    public VfPoint() {
        
    }
    
    public VfPoint(double x, double y) {
        this(null,x,y);
    }
    
    public VfPoint(VfPoint point) {
        this(point.getDescription(), point.getX(), point.getY());
    }
    
    public VfPoint(String description, double x, double y) {
        this.description = description;
    	this.x = x;
        this.y = y;
    }
    
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }
}
