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
    
    private static HashMap<String, PeakLoadShape> displayTextMap;
    static {
        displayTextMap = new HashMap<>();
        displayTextMap.put(PEAK_UNSPECIFIED.getDisplayText(), PEAK_UNSPECIFIED);
        displayTextMap.put(PEAK_STANDARD.getDisplayText(), PEAK_STANDARD);
        displayTextMap.put(PEAK_UNIFORM.getDisplayText(), PEAK_UNIFORM);
        displayTextMap.put(PEAK_SYMMETRIC.getDisplayText(), PEAK_SYMMETRIC);
    }
    
    public static PeakLoadShape getFromNameMap(String displayText) {
        return displayTextMap.get(displayText);
    }
    
    private String displayText;
    
    PeakLoadShape(String displayText) {
        this.displayText = displayText;
    }
    
    public String getDisplayText() {
        return displayText;
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
