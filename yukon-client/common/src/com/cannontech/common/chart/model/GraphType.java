package com.cannontech.common.chart.model;

import org.apache.commons.lang3.StringUtils;

public enum GraphType {
    LINE ("area"), 
    COLUMN ("column"),
    PIE("pie"),
    ;
    
    private final String highChartType;

    private GraphType(String highChartType) {
        this.highChartType = highChartType;
    }

    public String getHighChartType() {
        return highChartType;
    }

    /**
     * Returns the matching enum or null if no enum maches the string
     */
    public static GraphType fromString(String graphTypeStr) {
        if (StringUtils.isBlank(graphTypeStr)) {
            return null;
        }
        try {
            return GraphType.valueOf(graphTypeStr);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
