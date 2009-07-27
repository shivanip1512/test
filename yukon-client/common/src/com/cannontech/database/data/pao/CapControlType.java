package com.cannontech.database.data.pao;


public enum CapControlType {
    AREA("CCAREA"),
    SPECIAL_AREA("CCSPECIALAREA"),
    SUBSTATION("CCSUBSTATION"),
    SUBBUS("CCSUBBUS"),
    FEEDER("CCFEEDER"),
    CAPBANK("CAP BANK"),
    CBC("CBC"),
    SCHEDULE("SCHEDULE"),
    STRATEGY("STRATEGY");

	private final String displayValue;
	
	private CapControlType(String displayValue) {
		this.displayValue = displayValue;
	}
	
	public String getDisplayValue() {
		return displayValue;
	}
	
	static public CapControlType getCapControlType(String type) {
		for (CapControlType value : CapControlType.values()) {
			if (type.equals(value.displayValue)) {
				return value;
			}
		}
		throw new IllegalArgumentException();
	}
}
