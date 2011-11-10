package com.cannontech.database.data.pao;

import com.cannontech.common.util.DatabaseRepresentationSource;

public enum CapControlType implements DatabaseRepresentationSource {
    AREA("CCAREA", "Area"),
    SPECIAL_AREA("CCSPECIALAREA", "Special Area"),
    SUBSTATION("CCSUBSTATION", "Substation"),
    SUBBUS("CCSUBBUS", "Substation Bus"),
    FEEDER("CCFEEDER", "Feeder"),
    CAPBANK("CAP BANK", "Cap Bank"),
    CBC("CBC", "CBC"),
    SCHEDULE("SCHEDULE", "Schedule"),
    STRATEGY("STRATEGY", "Strategy"),
    LTC("LTC","Load Tap Changer"),
    GO_REGULATOR("GO_REGULATOR","Gang Operated Regulator"),
    PO_REGULATOR("PO_REGULATOR","Phase Operated Regulator");

    private final String dbValue;
	private final String displayValue;
	
	private CapControlType(String dbValue, String displayValue) {
		this.dbValue = dbValue;
		this.displayValue = displayValue;
	}
	
	public String getDisplayValue() {
		return displayValue;
	}
	
	static public CapControlType getCapControlType(String type) {
		for (CapControlType value : CapControlType.values()) {
			if (type.equals(value.dbValue)) {
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