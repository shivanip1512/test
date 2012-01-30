package com.cannontech.capcontrol.creation.model;

import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.DatabaseRepresentationSource;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public enum HierarchyImportResultType implements DatabaseRepresentationSource {
	SUCCESS("Success"),
	INVALID_PARENT("Invalid Parent Name"),
	INVALID_TYPE("Invalid Hierarchy Type"),
	INVALID_IMPORT_ACTION("Invalid Import Action"),
	INVALID_DISABLED_VALUE("Invalid Disabled Value"),
	INVALID_OPERATIONAL_STATE("Invalid Capbank Operational State"),
	MISSING_DATA("Missing Data"),
	OBJECT_EXISTS("Object Already Exists"),
	NO_SUCH_OBJECT("Object Doesn't Exist"),
	;
	
	private final String dbString;
	private final static Logger log = YukonLogManager.getLogger(HierarchyImportResultType.class);
	
	private final static ImmutableMap<String, HierarchyImportResultType> lookupByDbString;
	
	static {
		try {
			Builder<String, HierarchyImportResultType> dbBuilder = ImmutableMap.builder();
			for (HierarchyImportResultType action : values()) {
                dbBuilder.put(action.dbString, action);
            }
			lookupByDbString = dbBuilder.build();
        } catch (IllegalArgumentException e) {
            log.error("Caught exception while building lookup maps, look for a duplicate name or db string.", e);
            throw e;
        }
	}
	
	private HierarchyImportResultType(String dbString) {
		this.dbString = dbString;
	}
	
	public static HierarchyImportResultType getForDbString(String dbString) {
		HierarchyImportResultType action = lookupByDbString.get(dbString);
		Validate.notNull(action, dbString);
		return action;
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
}
