package com.cannontech.dr.nest.model;

import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.util.DatabaseRepresentationSource;

public enum LoadShapingPeak implements DatabaseRepresentationSource, DisplayableEnum {
   //The peak period load shaping.
    STANDARD, 
    UNIFORM, 
    SYMMETRIC;
    
    private static String baseKey = "yukon.web.modules.dr.nest.loadShapingType.";
    
    @Override
    public Object getDatabaseRepresentation() {
        return name();
    }
    
    @Override
    public String getFormatKey() {
        return baseKey + name();
    }
    
    static public LoadShapingPeak getLoadShapingPeak(Object value) {
        return LoadShapingPeak.valueOf(value.toString());
    }
}
