package com.cannontech.database.data.pao;

import com.cannontech.common.util.DatabaseRepresentationSource;

public enum CapControlType implements DatabaseRepresentationSource {
    AREA("CCAREA"),
    SPECIAL_AREA("CCSPECIALAREA"),
    SUBSTATION("CCSUBSTATION"),
    SUBBUS("CCSUBBUS"),
    FEEDER("CCFEEDER"),
    CAPBANK("CAP BANK"),
    CBC("CBC"),
    SCHEDULE("SCHEDULE"),
    STRATEGY("STRATEGY"),
    LTC("Load Tap Changer");

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

    @Override
    public Object getDatabaseRepresentation() {
        return getDisplayValue();
    }
}
