package com.cannontech.common.scheduledFileExport;

import com.cannontech.common.bulk.collection.device.DeviceCollection;

public class AdeSingleValueFileExportGenerationParameters implements ExportFileGenerationParameters {
	protected final DeviceCollection deviceCollection;
	protected final int formatId;
	
	public AdeSingleValueFileExportGenerationParameters(DeviceCollection deviceCollection, int formatId) {
		this.deviceCollection = deviceCollection;
		this.formatId = formatId;
	}

	@Override
	public ScheduledExportType getExportType() {
		return ScheduledExportType.ARCHIVED_DATA_EXPORT_ALL;
	}
	
	public DeviceCollection getDeviceCollection() {
		return deviceCollection;
	}

	public int getFormatId() {
		return formatId;
	}
	
	public String toString() {
		return "[FormatId: " + formatId 
				+ ", Device Collection: " + deviceCollection.toString() 
				+ "]";
	}
}
