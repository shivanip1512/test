package com.cannontech.database.data.point;

import com.cannontech.common.util.DatabaseRepresentationSource;

public enum PointArchiveType implements DatabaseRepresentationSource {

	NONE("None", "None"),
	ON_CHANGE("On Change", "On Change"),
	ON_TIMER("On Timer", "On Timer"), // added the 'r' to match db editor
	ON_UPDATE("On Update", "On Update"),
	ON_TIMER_OR_UPDATE("timer|update", "On Timer Or Update"); // UI calls this "On Timer Or Update"
	
	private String pointArchiveTypeName;
	private String displayName;
	
	PointArchiveType(String pointArchiveTypeName, String displayName) {
		this.pointArchiveTypeName = pointArchiveTypeName;
		this.displayName = displayName;
	}
	
	public String getPointArchiveTypeName() {
		return pointArchiveTypeName;
	}
	
	public String getDisplayName() {
		return displayName;
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
	
}
