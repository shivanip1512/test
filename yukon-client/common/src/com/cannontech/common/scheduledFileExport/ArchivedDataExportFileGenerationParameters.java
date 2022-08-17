package com.cannontech.common.scheduledFileExport;

import java.util.Set;

import com.cannontech.amr.archivedValueExporter.model.dataRange.DataRange;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.pao.attribute.model.Attribute;

public class ArchivedDataExportFileGenerationParameters implements ExportFileGenerationParameters {
	private final DeviceCollection deviceCollection;
	private final int formatId;
	private final Set<Attribute> attributes;
	private final DataRange dataRange;
	
	public ArchivedDataExportFileGenerationParameters(DeviceCollection deviceCollection, int formatId, Set<Attribute> attributes, DataRange dataRange) {
		this.deviceCollection = deviceCollection;
		this.formatId = formatId;
		this.attributes = attributes;
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

	public Set<Attribute> getAttributes() {
		return attributes;
	}

	public DataRange getDataRange() {
		return dataRange;
	}

	public String toString() {
		String returnString = "[FormatId: " + formatId + ", Device Collection: " + deviceCollection.toString() + ", ";
		for(Attribute attribute : attributes) {
			returnString += attribute.getMessage() + ", ";
		}
		returnString += " DataRange: " + dataRange + "]";
		
		return returnString;
	}
}
