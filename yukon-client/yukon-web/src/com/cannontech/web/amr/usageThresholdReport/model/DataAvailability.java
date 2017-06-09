package com.cannontech.web.amr.usageThresholdReport.model;

public enum DataAvailability {
    
    COMPLETE("#009933"), 
    PARTIAL("#4d90fe"),
    UNSUPPORTED("#ec971f"),
    NONE("#888");
    
    private DataAvailability(String color) {
        this.color = color;
    }

    private final String color;
    
    public String getColor() {
        return color;
    }

}
