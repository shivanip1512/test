package com.cannontech.capcontrol.creation.model;

import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.DatabaseRepresentationSource;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public enum ImportActionsEnum implements DatabaseRepresentationSource {
	ADD("Add"),
	UPDATE("Update"),
	REMOVE("Remove");
	
	private final String dbString;
	private final static Logger log = YukonLogManager.getLogger(ImportActionsEnum.class);
	
	private final static ImmutableMap<String, ImportActionsEnum> lookupByDbString;
	
	static {
		try {
			Builder<String, ImportActionsEnum> dbBuilder = ImmutableMap.builder();
			for (ImportActionsEnum action : values()) {
                dbBuilder.put(action.dbString, action);
            }
			lookupByDbString = dbBuilder.build();
        } catch (IllegalArgumentException e) {
            log.warn("Caught exception while building lookup maps, look for a duplicate name or db string.", e);
            throw e;
        }
	}
	
	private ImportActionsEnum(String dbString) {
		this.dbString = dbString;
	}
	
	public static ImportActionsEnum getForDbString(String dbString) {
		ImportActionsEnum action = lookupByDbString.get(dbString);
		Validate.notNull(action, dbString);
		return action;
	}
	
	public String getDbString() {
		return dbString;
	}
	
	@Override
	public Object getDatabaseRepresentation() {
		return dbString;
	}
}
