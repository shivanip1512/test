package com.cannontech.common.device.commands;


public enum CommandRequestExecutionType {
	
	DEVICE_COMMAND("Device Command", "A command sent to a device.", false),
	ROUTE_COMMAND("Route Command", "A command sent to a route.", false),
	DEVICE_ATTRIBUTE_READ("Device Attribute Read", "An attribute read command sent to a device.", false),
	GROUP_COMMAND("Group Command", "A command sent to a group of devices.", false),
	GROUP_ATTRIBUTE_READ("Group Attribute Read", "An attribute read command sent to a group of devices.", false),
	SCHEDULED_GROUP_COMMAND("Scheduled Group Command", "A command sent to a group of devices periodically.", true),
	SCHEDULED_GROUP_ATTRIBUTE_READ("Scheduled Group Attribute Read", "An attribute read command sent to a group of devices periodically.", true),
	
	OUTAGE_PROCESSING_OUTAGE_LOGS_READ("Outage Processing Outage Logs Read", "Outage logs read for the purpose of Outage Processing on the Outages Group of an Outage Monitor.", false),
	TAMPER_FLAG_PROCESSING_INTERNAL_STATUS_READ("Tamper Flag Processing Internal Flags Read", "Internal flags read for the purpose of Tamper Flag Processing on the Tamper Flag Group of a Tamper Flag Monitor.", false),
	TAMPER_FLAG_PROCESSING_INTERNAL_STATUS_RESET("Tamper Flag Processing Internal Flags Reset", "Internal flags reset for the purpose of Tamper Flag Processing on the Tamper Flag Group of a Tamper Flag Monitor.", false),
	
	
	DEVICE_CONFIG_VERIFY("Device Config Verify", "Verify command sent to a collection of devices for the purpose of Device Configuration.", false),
	DEVICE_CONFIG_SEND("Device Config Send", "Send config settings to a collection of devices for the purpose of Device Configuration.", false),
	DEVICE_CONFIG_READ("Device Config Read", "Send getconfig install command to a collection of devices for the purpose of Device Configuration.", false),
	;
	
	private String shortName;
	private String description;
	private boolean scheduled;
	
	CommandRequestExecutionType(String shortName, String description, boolean scheduled) {
		
		this.shortName = shortName;
		this.description = description;
		this.scheduled = scheduled;
	}
	
	public String getShortName() {
		return shortName;
	}
	
	public String getDescription() {
		return description;
	}
	
	public boolean isScheduled() {
		return scheduled;
	}
}
