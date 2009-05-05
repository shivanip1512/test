package com.cannontech.common.device.definition.model;

public enum DeviceFeature {

	TOU("TOU"),
	HIGH_BILL("High Bill"),
	OUTAGE("Outage"),
	MOVE_SUPPORTED("Move Supported"),
	LOAD_PROFILE("Load Profile"),
	PEAK_REPORT("Peak Report"),
	VOLTAGE("Voltage"),
	VOLTAGE_PROFILE("Voltage Profile"),
	;

	private String description;
	
	DeviceFeature(String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}
	
	public String getName() {
		return this.name();
	}
}
