package com.cannontech.common.scheduledFileExport;

import com.cannontech.amr.archivedValueExporter.model.dataRange.DataRange;
import com.cannontech.common.bulk.collection.device.DeviceCollection;
import com.cannontech.common.pao.attribute.model.Attribute;

public class ArchivedDataExportFileGenerationParameters implements ExportFileGenerationParameters {
	private final DeviceCollection deviceCollection;
	private final int formatId;
	private final Attribute attribute;
	private final DataRange dataRange;
	
	public ArchivedDataExportFileGenerationParameters(DeviceCollection deviceCollection, int formatId, Attribute attribute, DataRange dataRange) {
		this.deviceCollection = deviceCollection;
		this.formatId = formatId;
		this.attribute = attribute;
		this.dataRange = dataRange;
	}
	
	@Override
	public ScheduledExportType getExportType() {
		return ScheduledExportType.ARCHIVED_DATA_EXPORT;
	}
	
	public DeviceCollection getDeviceCollection() {
		return deviceCollection;
	}

	public int getFormatId() {
		return formatId;
	}

	public Attribute getAttribute() {
		return attribute;
	}

	public DataRange getDataRange() {
		return dataRange;
	}

	public String toString() {
		return "[FormatId: " + formatId 
				+ ", Device Collection: " + deviceCollection.toString() 
				+ ", Attribute: " + attribute.getMessage()
				+ ", DataRange: " + dataRange
				+ "]";
	}
}
