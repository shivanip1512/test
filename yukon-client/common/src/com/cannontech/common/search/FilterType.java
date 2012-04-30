package com.cannontech.common.search;

import com.cannontech.database.data.point.PointType;

public enum FilterType {

    STATUSPOINT,
    ANALOGPOINT;

    public static FilterType getForPointType(PointType pointType) {
        if (pointType == PointType.Status) {
            return STATUSPOINT;
        } else if (pointType == PointType.Analog) {
            return ANALOGPOINT;
        }
        
        throw new IllegalArgumentException("Unknown Filter Type for Point Type: " + pointType);
    }
    
}