package com.cannontech.dr.nest.model;

import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.util.DatabaseRepresentationSource;

public enum LoadShapingPreparation implements DatabaseRepresentationSource, DisplayableEnum {
    // The preparation period load shaping
    STANDARD, 
    NONE, 
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
    
    static public LoadShapingPreparation getLoadShapingPreparation(Object value) {
        return LoadShapingPreparation.valueOf(value.toString());
    }
}
