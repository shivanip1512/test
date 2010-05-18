package com.cannontech.common.pao.definition.model;

public enum PaoTag {

	TOU("TOU"),
	HIGH_BILL("High Bill"),
	OUTAGE("Outage"),
	MOVE_SUPPORTED("Move Supported"),
	LOAD_PROFILE("Load Profile"),
	PEAK_REPORT("Peak Report"),
	VOLTAGE("Voltage"),
	VOLTAGE_PROFILE("Voltage Profile"),
	DEVICE_CONFIGURATION_470("Device Configuration 470"),
	DEVICE_CONFIGURATION_430("Device Configuration 430"),
	DISCONNECT_410("410 Disconnect"),
	DISCONNECT_310("310 Disconnect"),
	DISCONNECT_213("213 Disconnect"),
	METER_DETAIL_DISPLAYABLE("Meter Detail Displayable"),
	PHASE_DETECT("Phase Detect"),
	STARS_ACCOUNT_ATTACHABLE_METER("Stars Account Attachable Meter"),
	LOCATE_ROUTE("Locate Route"),
	PORTER_COMMAND_REQUESTS("Porter Command Requests"),
	;

	private String description;
	
	PaoTag(String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}
	
	public String getName() {
		return this.name();
	}
}
