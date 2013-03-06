package com.cannontech.common.scheduledFileExport;

import java.util.List;

import com.cannontech.common.bulk.collection.device.DeviceCollection;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.google.common.collect.ImmutableList;

public class AdeMultiValueFileExportGenerationParameters extends AdeSingleValueFileExportGenerationParameters {
	private final List<Attribute> attributes;
	private final int daysPrevious;
	private final boolean lastRphId;
	
	public AdeMultiValueFileExportGenerationParameters(DeviceCollection deviceCollection, int formatId, List<Attribute> attributes) {
		super(deviceCollection, formatId);
		this.attributes = ImmutableList.copyOf(attributes);
		this.lastRphId = true;
		this.daysPrevious = 0;
	}
	
	public AdeMultiValueFileExportGenerationParameters(DeviceCollection deviceCollection, int formatId, List<Attribute> attributes, int daysPrevious) {
		super(deviceCollection, formatId);
		this.attributes = ImmutableList.copyOf(attributes);
		this.lastRphId = false;
		this.daysPrevious = daysPrevious;
	}
	
	@Override
	public ScheduledExportType getExportType() {
		return ScheduledExportType.ARCHIVED_DATA_EXPORT_FILTERED;
	}

	public List<Attribute> getAttributes() {
		return attributes;
	}

	public int getDaysPrevious() {
		return daysPrevious;
	}

	public boolean isLastRphId() {
		return lastRphId;
	}
	
	public String toString() {
		return "[FormatId: " + formatId 
				+ ", Attributes: " + attributes.toString()
				+ ", DaysPrevious: " + daysPrevious
				+ ", Use Last RPH ID: " + lastRphId
				+ ", Device Collection: " + deviceCollection.toString() 
				+ "]";
	}
}
