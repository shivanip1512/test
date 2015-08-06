package com.cannontech.web.tools.trends.model;

import java.util.List;

import com.cannontech.core.dynamic.PointValueHolder;

public class Series {

    private String name;
    private String color;
    private List<PointValueHolder> data;
    
    public String getName() {
    
        return name;
    }
    public void setName(String name) {
    
        this.name = name;
    }
    public String getColor() {
    
        return color;
    }
    public void setColor(String color) {
    
        this.color = color;
    }
    public List<PointValueHolder> getData() {
    
        return data;
    }
    public void setData(List<PointValueHolder> data) {
    
        this.data = data;
    }
    
    
}

