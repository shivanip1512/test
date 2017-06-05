package com.cannontech.web.amr.usageThresholdReport.model;

public enum ThresholdDesctiptor {
    
    LESS_OR_EQUAL("<="), 
    GREATOR_OR_EQUAL(">=");

    private String value;

    ThresholdDesctiptor(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
