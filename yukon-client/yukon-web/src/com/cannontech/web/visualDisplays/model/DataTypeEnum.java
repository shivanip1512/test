package com.cannontech.web.visualDisplays.model;

public enum DataTypeEnum {
	
	CURRENT_LOAD("Current Load"),
	CURRENT_IH("Current IH"),
	LOAD_TO_PEAK("Load To Peak"),
	PEAK_IH_LOAD("Peak IH Load"),
	PEAK_DAY_TIMESTAMP("Peak Day Timestamp");
	
	private String description;
	
	DataTypeEnum(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
}
