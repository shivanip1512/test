package com.cannontech.dr.nest.model;

import com.cannontech.common.util.DatabaseRepresentationSource;

public enum LoadShapingPreparation implements DatabaseRepresentationSource {
    // The preparation period load shaping
    STANDARD, 
    NONE, 
    RAMPING
    ;

    @Override
    public Object getDatabaseRepresentation() {
        return name();
    }
    
    static public LoadShapingPreparation getLoadShapingPreparation(Object value) {
        return LoadShapingPreparation.valueOf(value.toString());
    }
}
