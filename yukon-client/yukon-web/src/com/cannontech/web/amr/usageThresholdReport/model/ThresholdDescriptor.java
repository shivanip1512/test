package com.cannontech.web.amr.usageThresholdReport.model;

public enum ThresholdDescriptor {
    
    LESS_OR_EQUAL("<="), 
    GREATOR_OR_EQUAL(">=");

    private String value;

    ThresholdDescriptor(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
