package com.cannontech.web.stars.gateway.model;

// mockup model object for squences
public class SequenceData {
    
    private String name;
    private int start;
    private int end;
    private double percent;
    
    public SequenceData(String name, int start, int end, double percent) {
        this.name = name;
        this.start = start;
        this.end = end;
        this.percent = percent;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public int getStart() {
        return start;
    }
    
    public void setStart(int start) {
        this.start = start;
    }
    
    public int getEnd() {
        return end;
    }
    
    public void setEnd(int end) {
        this.end = end;
    }
    
    public double getPercent() {
        return percent;
    }
    
    public void setPercent(double percent) {
        this.percent = percent;
    }
    
    public boolean isWarning() {
        return percent >= 70.0 && percent < 90.0;
    }
    
    public boolean isError() {
        return percent < 70.0;
    }
}