package com.cannontech.database.data.point;

import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.util.DatabaseRepresentationSource;

public enum PointArchiveType implements DatabaseRepresentationSource, DisplayableEnum {

    NONE("None", "None", false),
    ON_CHANGE("On Change", "On Change", false), 
    ON_TIMER("On Timer", "On Timer", true),
    ON_UPDATE("On Update", "On Update", false),
    ON_TIMER_OR_UPDATE("timer|update", "On Timer Or Update", true);

    private String pointArchiveTypeName;
    private String displayName;
    private boolean isIntervalRequired;
    
    private static final String baseKey = "yukon.common.point.archiveType.";

    PointArchiveType(String pointArchiveTypeName, String displayName, boolean isIntervalRequired) {
        this.pointArchiveTypeName = pointArchiveTypeName;
        this.displayName = displayName;
        this.isIntervalRequired = isIntervalRequired;
    }

    public String getPointArchiveTypeName() {
        return pointArchiveTypeName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isIntervalRequired() {
        return isIntervalRequired;
    }

    public static PointArchiveType getByDisplayName(String name) {
        for (PointArchiveType value : PointArchiveType.values()) {
            if (value.getPointArchiveTypeName().equalsIgnoreCase(name)) {
                return value;
            }
        }
        return NONE;
    }

    @Override
    public Object getDatabaseRepresentation() {
        return this.pointArchiveTypeName;
    }

    @Override
    public String getFormatKey() {
        return baseKey + name();
    }

}
