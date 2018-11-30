package com.cannontech.dr.nest.model.v3;

import java.util.HashMap;

import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.util.DatabaseRepresentationSource;

public enum PrepLoadShape implements DatabaseRepresentationSource, DisplayableEnum {
    PREP_UNSPECIFIED("Unspecified"),
    PREP_STANDARD("Standard"),
    PREP_RAMPING("Ramping"),
    PREP_NONE("None")
    ;
    private static String baseKey = "yukon.web.modules.dr.nest.loadShapingType.";

    private static HashMap<String, PrepLoadShape> displayTextMap;
    static {
        displayTextMap = new HashMap<>();
        displayTextMap.put(PREP_UNSPECIFIED.getDisplayText(), PREP_UNSPECIFIED);
        displayTextMap.put(PREP_STANDARD.getDisplayText(), PREP_STANDARD);
        displayTextMap.put(PREP_RAMPING.getDisplayText(), PREP_RAMPING);
        displayTextMap.put(PREP_NONE.getDisplayText(), PREP_NONE);
    }
    
    public static PrepLoadShape getFromNameMap(String displayText) {
        return displayTextMap.get(displayText);
    }
    
    private String displayText;
    
    PrepLoadShape(String displayText) {
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
    
    static public PrepLoadShape getPrepLoadShape(Object value) {
        return PrepLoadShape.valueOf(value.toString());
    }
}
