package com.cannontech.common.device;

import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.DatabaseRepresentationSource;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public enum DeviceScanTypesEnum implements DatabaseRepresentationSource {
	GENERAL("General"),
	STATUS("Status"),
	EXCEPTION("Exception"),
	ACCUMULATOR("Accumulator"),
	INTEGRITY("Integrity"),
	;
	
	private final static ImmutableMap<String, DeviceScanTypesEnum> lookupByDbString;

	private final static Logger log = YukonLogManager.getLogger(DeviceScanTypesEnum.class);

	static {
        try {
            Builder<String, DeviceScanTypesEnum> dbBuilder = ImmutableMap.builder();
            for (DeviceScanTypesEnum scanType : values()) {
                dbBuilder.put(scanType.dbString, scanType);
            }
            lookupByDbString = dbBuilder.build();
        } catch (IllegalArgumentException e) {
            log.warn("Caught exception while building lookup maps, look for a duplicate name or db string.", e);
            throw e;
        }
    }
	
	private String dbString;
	
	private DeviceScanTypesEnum(String dbString) {
		this.dbString = dbString;
	}
	
	public static DeviceScanTypesEnum getForDbString(String dbString) {
		DeviceScanTypesEnum deviceType = lookupByDbString.get(dbString);
        Validate.notNull(deviceType, dbString);
        return deviceType;
	}

	@Override
	public Object getDatabaseRepresentation() {
		return dbString;
	}
}
