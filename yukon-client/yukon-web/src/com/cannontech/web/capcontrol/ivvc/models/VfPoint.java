package com.cannontech.web.capcontrol.ivvc.models;

public class VfPoint {
    
    double x;
    double y;
    
    public VfPoint() {
        
    }
    
    public VfPoint(double x, double y) {
        this.x = x;
        this.y = y;
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
