package com.cannontech.database.data.point;

import com.cannontech.common.util.DatabaseRepresentationSource;

public enum PointArchiveType implements DatabaseRepresentationSource {

	NONE("None"),
	ON_CHANGE("On Change"),
	ON_TIMER("On Timer"), // added the 'r' to match db editor
	ON_UPDATE("On Update"),
	ON_TIMER_OR_UPDATE("timer|update"); // UI calls this "On Timer Or Update"
	
	private String pointArchiveTypeName;
	
	PointArchiveType(String pointArchiveTypeName) {
		this.pointArchiveTypeName = pointArchiveTypeName;
	}
	
	public String getPointArchiveTypeName() {
		return pointArchiveTypeName;
	}
	
	public PointArchiveType getByDisplayName(String name) {
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
