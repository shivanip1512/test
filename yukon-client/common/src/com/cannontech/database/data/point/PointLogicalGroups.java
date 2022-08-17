package com.cannontech.database.data.point;

import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.util.DatabaseRepresentationSource;


/**
 * Class used for the logicalGroup fields inside points
 */
public enum PointLogicalGroups implements DisplayableEnum, DatabaseRepresentationSource {
    
    DEFAULT("Default"),
    SOE("SOE");
    
    private static final String baseKey = "yukon.common.point.logicalGroup.";
    
    private final String dbValue;
    
    PointLogicalGroups(String dbValue) {
        this.dbValue = dbValue;
    }
    
    public String getDbValue() {
        return dbValue;
    }
    
    public static boolean isValidLogicalGroup(String typeString) {
        
        for (PointLogicalGroups group : values()) {
            if (group.getDbValue().equals(typeString)) return true;
        }
        
        return false;
    }

    @Override
    public Object getDatabaseRepresentation() {
        return dbValue;
    }

    @Override
    public String getFormatKey() {
        return baseKey + name();
    }

}
