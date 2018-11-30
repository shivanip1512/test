package com.cannontech.dr.nest.model.v3;

import java.util.HashMap;

import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.util.DatabaseRepresentationSource;

public enum PostLoadShape implements DatabaseRepresentationSource, DisplayableEnum {
    POST_UNSPECIFIED("Unspecified"),
    POST_STANDARD("Standard"),
    POST_RAMPING("Ramping");
    private static String baseKey = "yukon.web.modules.dr.nest.loadShapingType.";
    
    private static HashMap<String, PostLoadShape> nameMap;
    static {
        nameMap = new HashMap<>();
        nameMap.put(POST_UNSPECIFIED.getDisplayText(), POST_UNSPECIFIED);
        nameMap.put(POST_STANDARD.getDisplayText(), POST_STANDARD);
        nameMap.put(POST_RAMPING.getDisplayText(), POST_RAMPING);
    }
    
    public static PostLoadShape getFromNameMap(String name) {
        return nameMap.get(name);
    }
    
    private String displayText;
    
    PostLoadShape(String displayText) {
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
    
    static public PostLoadShape getPostLoadShape(Object value) {
        return PostLoadShape.valueOf(value.toString());
    }
}
