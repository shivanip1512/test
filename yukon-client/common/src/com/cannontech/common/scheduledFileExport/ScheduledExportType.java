package com.cannontech.common.scheduledFileExport;

public enum ScheduledExportType {
	BILLING("Billing Schedule: "),
	ARCHIVED_DATA_EXPORT("Archived Data Export Schedule: "),
	WATER_LEAK("Water Leak Report Schedule: "),
	METER_EVENT("Meter Event Report Schedule: "),
	;
	
	private final String initiator;
	
	private ScheduledExportType(String initiator) {
		this.initiator = initiator;
	}
	
	public String getInitiator() {
		return initiator;
	}
}
