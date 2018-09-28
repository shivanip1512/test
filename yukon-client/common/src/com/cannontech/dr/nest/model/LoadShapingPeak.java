package com.cannontech.dr.nest.model;

import com.cannontech.common.util.DatabaseRepresentationSource;

public enum LoadShapingPeak implements DatabaseRepresentationSource {
   //The peak period load shaping.
    STANDARD, 
    UNIFORM, 
    SYMMETRIC;
    
    @Override
    public Object getDatabaseRepresentation() {
        return name();
    }
    
    static public LoadShapingPeak getLoadShapingPeak(Object value) {
        return LoadShapingPeak.valueOf(value.toString());
    }
}
