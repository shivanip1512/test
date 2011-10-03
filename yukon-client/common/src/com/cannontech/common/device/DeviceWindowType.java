package com.cannontech.common.device;

import org.apache.commons.lang.Validate;

import com.cannontech.common.util.DatabaseRepresentationSource;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public enum DeviceWindowType implements DatabaseRepresentationSource {
	SCAN("SCAN"),
	PEAK("PEAK"),
	ALT_RATE("ALTERNATE RATE"),
	;
	
	private final static ImmutableMap<String, DeviceWindowType> lookupByDbString;

	static {
        Builder<String, DeviceWindowType> dbBuilder = ImmutableMap.builder();
        for (DeviceWindowType scanType : values()) {
            dbBuilder.put(scanType.dbString, scanType);
        }
        lookupByDbString = dbBuilder.build();
    }
	
	private String dbString;
	
	private DeviceWindowType(String dbString) {
		this.dbString = dbString;
	}
	
	public static DeviceWindowType getForDbString(String dbString) {
		DeviceWindowType deviceType = lookupByDbString.get(dbString);
        Validate.notNull(deviceType, dbString);
        return deviceType;
	}

	@Override
	public Object getDatabaseRepresentation() {
		return dbString;
	}
}
