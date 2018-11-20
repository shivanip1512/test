package com.cannontech.dr.nest.model.v3;

import java.util.HashMap;

import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.util.DatabaseRepresentationSource;

public enum PeakLoadShape implements DatabaseRepresentationSource, DisplayableEnum {
    PEAK_UNSPECIFIED("Unspecified"),
    PEAK_STANDARD("Standard"),
    PEAK_UNIFORM("Uniform"),
    PEAK_SYMMETRIC("Symmetric")
    ;
    private static String baseKey = "yukon.web.modules.dr.nest.loadShapingType.";
    
    private static HashMap<String, PeakLoadShape> nameMap;
    static {
        nameMap = new HashMap<>();
        nameMap.put(PEAK_UNSPECIFIED.getName(), PEAK_UNSPECIFIED);
        nameMap.put(PEAK_STANDARD.getName(), PEAK_STANDARD);
        nameMap.put(PEAK_UNIFORM.getName(), PEAK_UNIFORM);
        nameMap.put(PEAK_SYMMETRIC.getName(), PEAK_SYMMETRIC);
    }
    
    public static PeakLoadShape getFromNameMap(String name) {
        return nameMap.get(name);
    }
    
    private String name;
    
    PeakLoadShape(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    @Override
    public String getFormatKey() {
        return baseKey + name();
    }

    @Override
    public Object getDatabaseRepresentation() {
        return name();
    }
    
    static public PeakLoadShape getPeakLoadShape(Object value) {
        return PeakLoadShape.valueOf(value.toString());
    }
}
