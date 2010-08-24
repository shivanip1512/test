package com.cannontech.capcontrol;

import com.cannontech.common.util.DatabaseRepresentationSource;

public enum ControlMethod implements DatabaseRepresentationSource{
	INDIVIDUAL_FEEDER("IndividualFeeder", "Individual Feeder"),
	SUBSTATION_BUS("SubstationBus", "Substation Bus"),
	BUSOPTIMIZED_FEEDER("BusOptimizedFeeder", "Bus Optimized Feeder"),
	MANUAL_ONLY("ManualOnly", "Manual Only"),
	TIME_OF_DAY("TimeOfDay", "Time of Day"),
	NONE("NONE", "NONE");
	
	private String dbName;
	private String displayName;
	private ControlMethod(String dbName, String displayName) {
		this.dbName = dbName;
		this.displayName = displayName;
	}
	
	public static ControlMethod getForDbString(String type){
		for(ControlMethod controlMethod : ControlMethod.values()) {
			if( controlMethod.getDatabaseRepresentation().equals(type)) {
				return controlMethod;
			}
		}
		throw new IllegalArgumentException("Invalid control method: " + type);
	}
	
	@Override
	public String getDatabaseRepresentation() {
		return dbName;
	}
	
	public String getDisplayName() {
        return displayName;
    }
	
}