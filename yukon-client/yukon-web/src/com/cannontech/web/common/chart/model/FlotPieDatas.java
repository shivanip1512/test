package com.cannontech.web.common.chart.model;

/**
 * This data object is used when creating a Pie Chart when you want to
 * specify particular colors for each label key in the labelValueMap.
 * Pass in the data and the color in  either rgb or hex format.
 * If null is passed for the color, flot chooses a random color for it.
 */
public class FlotPieDatas {
    
    private Integer data;
    private String color;
    
    public FlotPieDatas(Integer data, String color) {
        this.data = data;
        this.color = color;
    }
    
    public Integer getData() {
        return data;
    }
    public void setData(Integer data) {
        this.data = data;
    }
    public String getColor() {
        return color;
    }
    public void setColor(String color) {
        this.color = color;
    }
    
}
