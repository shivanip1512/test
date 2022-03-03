package com.cannontech.common.scheduledFileExport;

import java.util.Set;

import com.cannontech.amr.archivedValueExporter.model.dataRange.DataRange;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.util.TimeIntervals;

public class ArchivedDataExportFileGenerationParameters implements ExportFileGenerationParameters {
	private final DeviceCollection deviceCollection;
	private final int formatId;
	private final Set<Attribute> attributes;
	private final DataRange dataRange;
	private final boolean onInterval;
	private final TimeIntervals interval;
	
	public ArchivedDataExportFileGenerationParameters(DeviceCollection deviceCollection, int formatId, 
	        Set<Attribute> attributes, DataRange dataRange, boolean onInterval, TimeIntervals interval) {
	    
		this.deviceCollection = deviceCollection;
		this.formatId = formatId;
		this.attributes = attributes;
		this.dataRange = dataRange;
		this.onInterval = onInterval;
		this.interval = interval;
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

	public boolean isOnInterval() {
        return onInterval;
    }

    public TimeIntervals getInterval() {
        return interval;
    }

    @Override
    public String toString() {
        return String.format("ArchivedDataExportFileGenerationParameters [deviceCollection=%s, formatId=%s, attributes=%s, dataRange=%s, onInterval=%s, interval=%s]",
                             deviceCollection,
                             formatId,
                             attributes,
                             dataRange,
                             onInterval,
                             interval);
    }
}
