package com.cannontech.capcontrol.creation.model;

import org.apache.logging.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.DatabaseRepresentationSource;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public enum IvvcZoneImportResultType implements DatabaseRepresentationSource {
    //TODO: Add more results for IVVC Zone Import
	SUCCESS("Success", 0),
	;
	
	private final String dbString;
	private final int errorCode;
	
	private final static Logger log = YukonLogManager.getLogger(IvvcZoneImportResultType.class);
	
	private final static ImmutableMap<String, IvvcZoneImportResultType> lookupByDbString;
	
	static {
		try {
			Builder<String, IvvcZoneImportResultType> dbBuilder = ImmutableMap.builder();
			for (IvvcZoneImportResultType action : values()) {
                dbBuilder.put(action.dbString, action);
            }
			lookupByDbString = dbBuilder.build();
        } catch (IllegalArgumentException e) {
            log.error("Caught exception while building lookup maps, look for a duplicate name or db string.", e);
            throw e;
        }
	}
	
	private IvvcZoneImportResultType(String dbString, int errorCode) {
		this.dbString = dbString;
		this.errorCode = errorCode;
	}
	
	public Boolean isSuccess() {
		return this == SUCCESS;
	}
	
	public String getDbString() {
		return dbString;
	}
	
	@Override
	public Object getDatabaseRepresentation() {
		return dbString;
	}

    public int getErrorCode() {
        return errorCode;
    }
}
