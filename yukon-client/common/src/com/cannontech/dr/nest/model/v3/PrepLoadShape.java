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

    private static HashMap<String, PrepLoadShape> nameMap;
    static {
        nameMap = new HashMap<>();
        nameMap.put(PREP_UNSPECIFIED.getName(), PREP_UNSPECIFIED);
        nameMap.put(PREP_STANDARD.getName(), PREP_STANDARD);
        nameMap.put(PREP_RAMPING.getName(), PREP_RAMPING);
        nameMap.put(PREP_NONE.getName(), PREP_NONE);
    }
    
    public static PrepLoadShape getFromNameMap(String name) {
        return nameMap.get(name);
    }
    
    private String name;
    
    PrepLoadShape(String name) {
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
    
    static public PrepLoadShape getPrepLoadShape(Object value) {
        return PrepLoadShape.valueOf(value.toString());
    }
}
