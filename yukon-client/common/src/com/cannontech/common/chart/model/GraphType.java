package com.cannontech.common.chart.model;

public enum GraphType {
    LINE ("line"), 
    COLUMN ("bar");
    
    private final String flotType;

    private GraphType(String flotType) {
        this.flotType = flotType;
    }

    public String getFlotType() {
        return flotType;
    }
}
