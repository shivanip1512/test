package com.cannontech.common.device;

import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.DatabaseRepresentationSource;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public enum DeviceWindowTypesEnum implements DatabaseRepresentationSource {
	SCAN("SCAN"),
	PEAK("PEAK"),
	ALT_RATE("ALTERNATE RATE"),
	;
	
	private final static ImmutableMap<String, DeviceWindowTypesEnum> lookupByDbString;

	private final static Logger log = YukonLogManager.getLogger(DeviceWindowTypesEnum.class);

	static {
        try {
            Builder<String, DeviceWindowTypesEnum> dbBuilder = ImmutableMap.builder();
            for (DeviceWindowTypesEnum scanType : values()) {
                dbBuilder.put(scanType.dbString, scanType);
            }
            lookupByDbString = dbBuilder.build();
        } catch (IllegalArgumentException e) {
            log.warn("Caught exception while building lookup maps, look for a duplicate name or db string.", e);
            throw e;
        }
    }
	
	private String dbString;
	
	private DeviceWindowTypesEnum(String dbString) {
		this.dbString = dbString;
	}
	
	public static DeviceWindowTypesEnum getForDbString(String dbString) {
		DeviceWindowTypesEnum deviceType = lookupByDbString.get(dbString);
        Validate.notNull(deviceType, dbString);
        return deviceType;
	}

	@Override
	public Object getDatabaseRepresentation() {
		return dbString;
	}
}
