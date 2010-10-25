package com.cannontech.capcontrol;


public enum ControlAlgorithm {
	
    NONE("NONE"),
	KVAR("kVAr"),
	VOLTS("VOLTS"),
	MULTI_VOLT("Multi Volt"),
	MULTI_VOLT_VAR("Multi Volt Var"),
	PFACTOR_KW_KVAR("P-Factor kW/kVAr"),
	INTEGRATED_VOLT_VAR("Integrated Volt/Var"),
	TIME_OF_DAY("Time of Day");
	
	private final String displayName;
	
	private ControlAlgorithm(String name) {
		this.displayName = name;
	}
	
	public String getDisplayName() {
		return displayName;
	}
	
	static public ControlAlgorithm getDisplayName(String displayName) {
		for (ControlAlgorithm value : ControlAlgorithm.values()) {
			if (displayName.equals(value.displayName)) {
				return value;
			}
		}
		throw new IllegalArgumentException();
	}
	
};