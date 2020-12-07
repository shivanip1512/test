package com.cannontech.common.chart.model;

import org.apache.commons.lang3.StringUtils;

public enum GraphType {
    LINE ("line", "area"), 
    COLUMN ("bar", "column"),
    PIE("pie", "pie"),
    ;
    
    private final String flotType;
    private final String highChartType;

    private GraphType(String flotType, String highChartType) {
        this.flotType = flotType;
        this.highChartType = highChartType;
    }

    public String getFlotType() {
        return flotType;
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
