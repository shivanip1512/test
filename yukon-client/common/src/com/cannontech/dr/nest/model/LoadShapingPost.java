package com.cannontech.dr.nest.model;

import com.cannontech.common.util.DatabaseRepresentationSource;

public enum LoadShapingPost implements DatabaseRepresentationSource {
    // The post-peak period load shaping.
    STANDARD,
    RAMPING
    ;
    
    @Override
    public Object getDatabaseRepresentation() {
        return name();
    }
    
    static public LoadShapingPost getLoadShapingPost(Object value) {
        return LoadShapingPost.valueOf(value.toString());
    }
}
