package com.cannontech.capcontrol.creation.model;

import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.DatabaseRepresentationSource;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public enum ImportAction implements DatabaseRepresentationSource {
	ADD("Add"),
	UPDATE("Update"),
	REMOVE("Remove");
	
	private final String dbString;
	private final static Logger log = YukonLogManager.getLogger(ImportAction.class);
	
	private final static ImmutableMap<String, ImportAction> lookupByDbString;
	
	static {
		try {
			Builder<String, ImportAction> dbBuilder = ImmutableMap.builder();
			for (ImportAction action : values()) {
                dbBuilder.put(action.dbString.toLowerCase(), action);
            }
			lookupByDbString = dbBuilder.build();
        } catch (IllegalArgumentException e) {
            log.error("Caught exception while building lookup maps, look for a duplicate name or db string.", e);
            throw e;
        }
	}
	
	private ImportAction(String dbString) {
		this.dbString = dbString;
	}
	
	public static ImportAction getForDbString(String dbString) {
		ImportAction action = lookupByDbString.get(dbString.toLowerCase());
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
