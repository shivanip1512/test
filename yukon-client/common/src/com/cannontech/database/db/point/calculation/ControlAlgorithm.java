package com.cannontech.database.db.point.calculation;


public enum ControlAlgorithm {
	 
	KVAR("kVAr"),
	VOLTS("VOLTS"),
	MULTIVOLT("Multi Volt"),
	MULTIVOLTVAR("Multi Volt Var"),
	PFACTORKWKVAR("P-Factor kW/kVAr");
	
	private final String displayName;
	
	private ControlAlgorithm(String name) {
		this.displayName = name;
	}
	
	public String getDisplayName() {
		return displayName;
	}
	
	static public ControlAlgorithm getControlAlgorithm(String commandName) {
		for (ControlAlgorithm value : ControlAlgorithm.values()) {
			if (commandName.equals(value.displayName)) {
				return value;
			}
		}
		throw new IllegalArgumentException();
	}
	
};