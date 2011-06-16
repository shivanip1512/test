package com.cannontech.common.bulk.model;

import org.joda.time.Interval;

public class ReadSequence {
    
    private int width = 1;
    private String color;
    private Interval range;

    public ReadSequence(int width, String color, Interval range) {
        this.width = width;
        this.color = color;
        this.range = range;
    }
    
    public int getWidth() {
        return width;
    }
    
    public String getColor() {
        return color;
    }
    
    public Interval getRange() {
        return range;
    }
    
}