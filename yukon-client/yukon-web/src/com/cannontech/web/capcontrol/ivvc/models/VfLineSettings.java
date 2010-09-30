package com.cannontech.web.capcontrol.ivvc.models;

public class VfLineSettings {
    
    int id;
    String color;
    int fillAlpha = 0;
    boolean minMax = true;
    
    public VfLineSettings(int id, String color, int fillAlpha, boolean minMax) {
        this.id = id;
        this.color = color;
        this.fillAlpha = fillAlpha;
        this.minMax = minMax;
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getColor() {
        return color;
    }
    
    /**
     * Hex value for the color of the line and fillAlpha
     * @param color
     */
    public void setColor(String color) {
        this.color = color;
    }

    public int getFillAlpha() {
        return fillAlpha;
    }

    /**
     * Fill Alpha is the intensity of the color used to fill.
     * 0 - 100. 0 is no fill, 100 is solid.
     * 
     * Uses the color set in this object.
     * @param fillAlpha
     */
    public void setFillAlpha(int fillAlpha) {
        this.fillAlpha = fillAlpha;
    }

    public boolean isMinMax() {
        return minMax;
    }

    public void setMinMax(boolean minMax) {
        this.minMax = minMax;
    }
}
