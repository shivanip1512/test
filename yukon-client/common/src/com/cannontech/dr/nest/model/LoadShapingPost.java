package com.cannontech.dr.nest.model;

import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.util.DatabaseRepresentationSource;

public enum LoadShapingPost implements DatabaseRepresentationSource, DisplayableEnum {
    // The post-peak period load shaping.
    STANDARD,
    RAMPING
    ;
    private static String baseKey = "yukon.web.modules.dr.nest.loadShapingType.";
    
    @Override
    public Object getDatabaseRepresentation() {
        return name();
    }
    
    @Override
    public String getFormatKey() {
        return baseKey + name();
    }
    
    static public LoadShapingPost getLoadShapingPost(Object value) {
        return LoadShapingPost.valueOf(value.toString());
    }
}
