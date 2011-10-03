package com.cannontech.capcontrol;

import org.apache.commons.lang.Validate;

import com.cannontech.common.util.DatabaseRepresentationSource;
import com.google.common.collect.ImmutableMap;

public enum CapBankOperationalState implements DatabaseRepresentationSource{
    FIXED("Fixed"),
    SWITCHED("Switched"),
    STANDALONE("StandAlone"),
    UNINSTALLED("Uninstalled"),
    ;
    
    private String dbString;
    
    private final static ImmutableMap<String, CapBankOperationalState> lookupByString;
	
	static {
        ImmutableMap.Builder<String, CapBankOperationalState> stringBuilder = ImmutableMap.builder();
        for (CapBankOperationalState state : values()) {
            stringBuilder.put(state.getDbString(), state);
        }
        lookupByString = stringBuilder.build();
    }
	
	public static CapBankOperationalState getStateByName(String name) {
		CapBankOperationalState state = lookupByString.get(name);
		Validate.notNull(state, name);
		return state;
	}
	
	private CapBankOperationalState(String dbString) {
		this.dbString = dbString;
	}
	
	public String getDbString() {
		return dbString;
	}

	@Override
	public Object getDatabaseRepresentation() {
		return dbString;
	}
}
