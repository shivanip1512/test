package com.cannontech.amr.statusPointProcessing.model;

public enum StatusPointMonitorStateType {
    DONT_CARE,
    DIFFERENCE,
    EXACT,
    ;
    
    public boolean isDontCare() {
        return this == DONT_CARE;
    }
    
    public boolean isDifference() {
        return this == DIFFERENCE;
    }
    
    public boolean isExact() {
        return this == EXACT;
    }
}