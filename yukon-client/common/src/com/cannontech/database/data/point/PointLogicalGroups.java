package com.cannontech.database.data.point;

import static com.google.common.base.Preconditions.checkArgument;

import org.apache.logging.log4j.core.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.util.DatabaseRepresentationSource;
import com.google.common.collect.ImmutableMap;


/**
 * Class used for the logicalGroup fields inside points
 */
public enum PointLogicalGroups implements DisplayableEnum, DatabaseRepresentationSource {
    
    DEFAULT("Default"),
    SOE("SOE");
    
    private static final String baseKey = "yukon.common.point.logicalGroup.";
    private static final Logger log = YukonLogManager.getLogger(PointLogicalGroups.class);
    private final String dbValue;
    
    PointLogicalGroups(String dbValue) {
        this.dbValue = dbValue;
    }
    
    public String getDbValue() {
        return dbValue;
    }
    
    private final static ImmutableMap<String, PointLogicalGroups> lookupByLogicalGroup;
    static {
        try {
            ImmutableMap.Builder<String, PointLogicalGroups> LogicalGroupeBuilder = ImmutableMap.builder();
            for (PointLogicalGroups logicalGroups : values()) {
                LogicalGroupeBuilder.put(logicalGroups.dbValue, logicalGroups);
            }
            lookupByLogicalGroup = LogicalGroupeBuilder.build();
        } catch (IllegalArgumentException e) {
            log.warn("Caught exception while building lookup maps, look for a duplicate name or db string.", e);
            throw e;
        }
    }

    public static PointLogicalGroups getLogicalGroupValue(String value) {
        PointLogicalGroups logicalGroups = lookupByLogicalGroup.get(value);
        checkArgument(logicalGroups != null, logicalGroups);
        return logicalGroups;
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
