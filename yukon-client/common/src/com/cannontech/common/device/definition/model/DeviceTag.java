package com.cannontech.common.device.definition.model;

public enum DeviceTag {

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
	;

	private String description;
	
	DeviceTag(String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}
	
	public String getName() {
		return this.name();
	}
}
