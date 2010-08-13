package com.cannontech.capcontrol;

import com.cannontech.common.util.DatabaseRepresentationSource;

public enum ControlMethod implements DatabaseRepresentationSource{
	INDIVIDUAL_FEEDER("IndividualFeeder"),
	SUBSTATION_BUS("SubstationBus"),
	BUSOPTIMIZED_FEEDER("BusOptimizedFeeder"),
	MANUAL_ONLY("ManualOnly"),
	TIME_OF_DAY("TimeOfDay"),
	NONE("NONE");
	
	private String dbName;
	private ControlMethod(String type) {
		this.dbName = type;
	}
	
	public static ControlMethod getForDbString(String type){
		for(ControlMethod controlMethod : ControlMethod.values()) {
			if( controlMethod.getDatabaseRepresentation().equalsIgnoreCase(type)) {
				return controlMethod;
			}
		}
		throw new IllegalArgumentException("Invalid control method: " + type);
	}
	
	@Override
	public String getDatabaseRepresentation() {
		return dbName;
	}
}