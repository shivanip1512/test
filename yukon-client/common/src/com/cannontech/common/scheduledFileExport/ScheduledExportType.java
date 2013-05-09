package com.cannontech.common.scheduledFileExport;

public enum ScheduledExportType {
	BILLING(true),
	ARCHIVED_DATA_EXPORT(true),
	WATER_LEAK(false),
	METER_EVENT(false),
	;
	
	boolean isPersistedFormat;
	
	private ScheduledExportType(boolean isPersistedFormat) {
	    this.isPersistedFormat = isPersistedFormat;
	}
	
	public boolean isPersistedFormat() {
	    return isPersistedFormat;
	}
}
