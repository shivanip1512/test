package com.cannontech.web.capcontrol.ivvc.models;

import java.util.List;

public class VfLineData {
    
    int id;
    List<VfPoint> points;
    
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

}
