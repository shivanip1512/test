package com.cannontech.common.chart.model;

public class FlotBarOptions {
    private boolean show;
    private String align;
    private boolean fill;
    private String fillColor;
    
    
    public FlotBarOptions() {}
    public FlotBarOptions(boolean show, String align, boolean fill, String fillColor) {
        super();
        this.show = show;
        this.align = align;
        this.fill = fill;
        this.fillColor = fillColor;
    }

    public boolean isShow() {
        return show;
    }

    public void setShow(boolean show) {
        this.show = show;
    }

    public String getAlign() {
        return align;
    }

    public void setAlign(String align) {
        this.align = align;
    }

    public boolean isFill() {
        return fill;
    }

    public void setFill(boolean fill) {
        this.fill = fill;
    }

    public String getFillColor() {
        return fillColor;
    }

    public void setFillColor(String fillColor) {
        this.fillColor = fillColor;
    }

}
