package com.cannontech.capcontrol.creation.model;

import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.DatabaseRepresentationSource;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public enum CbcImportResultType implements DatabaseRepresentationSource {
	SUCCESS("Success", 0),
	INVALID_PARENT("Invalid Parent Name", 1),
	INVALID_TYPE("Invalid Type", 2),
	INVALID_IMPORT_ACTION("Invalid Import Action", 3),
	MISSING_DATA("Missing Data", 6),
	OBJECT_EXISTS("Object Already Exists", 7),
	NO_SUCH_OBJECT("Object Doesn't Exist", 8),
	INVALID_COMM_CHANNEL("Invalid Comm Channel", 9),
	;
	
	private final String dbString;
	private final int errorCode;
	
	private final static Logger log = YukonLogManager.getLogger(CbcImportResultType.class);
	
	private final static ImmutableMap<String, CbcImportResultType> lookupByDbString;
	
	static {
		try {
			Builder<String, CbcImportResultType> dbBuilder = ImmutableMap.builder();
			for (CbcImportResultType action : values()) {
                dbBuilder.put(action.dbString, action);
            }
			lookupByDbString = dbBuilder.build();
        } catch (IllegalArgumentException e) {
            log.warn("Caught exception while building lookup maps, look for a duplicate name or db string.", e);
            throw e;
        }
	}
	
	private CbcImportResultType(String dbString, int errorCode) {
		this.dbString = dbString;
		this.errorCode = errorCode;
	}
	
	public static CbcImportResultType getForDbString(String dbString) {
		CbcImportResultType action = lookupByDbString.get(dbString);
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

    public int getErrorCode() {
        return errorCode;
    }
}
