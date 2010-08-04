package com.cannontech.capcontrol;

public enum ControlMethod {
	INDIVIDUAL_FEEDER("IndividualFeeder"),
	SUBSTATION_BUS("SubstationBus"),
	BUSOPTIMIZED_FEEDER("BusOptimizedFeeder"),
	MANUAL_ONLY("ManualOnly"),
	TIME_OF_DAY("TimeOfDay");
	
	private String dbName;
	private ControlMethod(String type) {
		this.dbName = type;
	}
	
	public static ControlMethod getForDbString(String type){
		for(ControlMethod controlMethod : ControlMethod.values()) {
			if( controlMethod.getDbName().equalsIgnoreCase(type)) {
				return controlMethod;
			}
		}
		throw new IllegalArgumentException("Invalid control method: " + type);
	}
	
	public String getDbName() {
		return dbName;
	}
}