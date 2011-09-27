package com.cannontech.capcontrol.creation.model;

import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.DatabaseRepresentationSource;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public enum CbcImportResultTypesEnum implements DatabaseRepresentationSource {
	SUCCESS("Success"),
	MISSING_DATA("Missing Data"),
	INVALID_COMM_CHANNEL("Invalid Comm Channel"),
	INVALID_IMPORT_ACTION("Invalid Import Action"),
	INVALID_TYPE("Invalid Type"),
	INVALID_PARENT("Invalid Parent"),
	DEVICE_EXISTS("Device Already Exists"),
	NO_SUCH_OBJECT("Object Doesn't Exist"),
	;
	
	private final String dbString;
	private final static Logger log = YukonLogManager.getLogger(CbcImportResultTypesEnum.class);
	
	private final static ImmutableMap<String, CbcImportResultTypesEnum> lookupByDbString;
	
	static {
		try {
			Builder<String, CbcImportResultTypesEnum> dbBuilder = ImmutableMap.builder();
			for (CbcImportResultTypesEnum action : values()) {
                dbBuilder.put(action.dbString, action);
            }
			lookupByDbString = dbBuilder.build();
        } catch (IllegalArgumentException e) {
            log.warn("Caught exception while building lookup maps, look for a duplicate name or db string.", e);
            throw e;
        }
	}
	
	private CbcImportResultTypesEnum(String dbString) {
		this.dbString = dbString;
	}
	
	public static CbcImportResultTypesEnum getForDbString(String dbString) {
		CbcImportResultTypesEnum action = lookupByDbString.get(dbString);
		Validate.notNull(action, dbString);
		return action;
	}
	
	public Boolean isSuccess() {
		return this == SUCCESS;
	}
	
	@Override
	public Object getDatabaseRepresentation() {
		return dbString;
	}
	
	public String getDbString() {
		return dbString;
	}
}
