package com.cannontech.database.data.pao;

import com.cannontech.common.util.DatabaseRepresentationSource;

public enum VoltageRegulatorType implements DatabaseRepresentationSource {
	LOAD_TAP_CHANGER("Load Tap Changer", "Load Tap Changer"), //left as lower case b/c that is how it is in DB currently
	GANG_OPERATED("GANGOPERATED", "Gang Operated"),
	PHASE_OPERATED("PHASEOPERATED", "Phase Operated"),
	;
	
	private String dbValue;
	private String displayValue;
    
    VoltageRegulatorType (String dbValue, String displayValue) {
        this.dbValue = dbValue;
    	this.displayValue = displayValue;
    }
    
    public String getDisplayValue() {
    	return displayValue;
    }
    
    static public VoltageRegulatorType getForDbString(String dbType) {
		for (VoltageRegulatorType value : VoltageRegulatorType.values()) {
			if (dbType.equals(value.dbValue)) {
				return value;
			}
		}
		throw new IllegalArgumentException();
	}
    
    public String getDbValue() {
        return dbValue;
    }
    
    @Override
    public Object getDatabaseRepresentation() {
    	return dbValue;
    }
}