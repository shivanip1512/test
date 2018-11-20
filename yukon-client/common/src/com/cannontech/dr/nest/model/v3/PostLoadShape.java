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
        nameMap.put(POST_UNSPECIFIED.getName(), POST_UNSPECIFIED);
        nameMap.put(POST_STANDARD.getName(), POST_STANDARD);
        nameMap.put(POST_RAMPING.getName(), POST_RAMPING);
    }
    
    public static PostLoadShape getFromNameMap(String name) {
        return nameMap.get(name);
    }
    
    private String name;
    
    PostLoadShape(String name) {
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
    
    static public PostLoadShape getPostLoadShape(Object value) {
        return PostLoadShape.valueOf(value.toString());
    }
}
