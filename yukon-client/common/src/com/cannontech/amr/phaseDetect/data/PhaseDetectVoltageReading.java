package com.cannontech.amr.phaseDetect.data;

public class PhaseDetectVoltageReading {

    private Double initial;
    private Double last;
    private Double delta;
    
    public Double getInitial() {
        return initial;
    }
    
    public void setInitial(Double initial) {
        this.initial = initial;
    }
    
    public Double getLast() {
        return last;
    }
    
    public void setLast(Double last) {
        this.last = last;
    }
    
    public Double getDelta() {
        return delta;
    }
    
    public void setDelta(Double delta) {
        this.delta = delta;
    }
    
}
