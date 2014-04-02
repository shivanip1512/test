package com.cannontech.common.chart.model;

import org.apache.commons.lang3.StringUtils;

public enum GraphType {
    LINE ("line"), 
    COLUMN ("bar"),
    PIE("pie"),
    ;
    
    private final String flotType;

    private GraphType(String flotType) {
        this.flotType = flotType;
    }

    public String getFlotType() {
        return flotType;
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
