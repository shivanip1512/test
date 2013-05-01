package com.cannontech.common.scheduledFileExport;

import java.util.Set;

import com.cannontech.common.bulk.collection.device.DeviceCollection;
import com.cannontech.common.pao.attribute.model.Attribute;

public class MeterEventsExportGenerationParameters implements ExportFileGenerationParameters {
	private final int daysPrevious;
	private final boolean onlyLatestEvent;
	private final boolean onlyAbnormalEvents;
	private final boolean includeDisabledDevices;
	private final DeviceCollection deviceCollection;
	private final Set<Attribute> attributes;

	public MeterEventsExportGenerationParameters(int daysPrevious, boolean onlyLatestEvent, boolean onlyAbnormalEvents, 
			boolean includeDisabledDevices, DeviceCollection deviceCollection, Set<Attribute> attributes) {
		this.daysPrevious = daysPrevious;
		this.onlyAbnormalEvents = onlyAbnormalEvents;
		this.onlyLatestEvent = onlyLatestEvent;
		this.includeDisabledDevices = includeDisabledDevices;
		this.deviceCollection = deviceCollection;
		this.attributes = attributes;
	}
	
	@Override
	public ScheduledExportType getExportType() {
		return ScheduledExportType.METER_EVENT;
	}
	
	public int getDaysPrevious() {
		return daysPrevious;
	}

	public boolean isOnlyLatestEvent() {
		return onlyLatestEvent;
	}

	public boolean isOnlyAbnormalEvents() {
		return onlyAbnormalEvents;
	}

	public boolean isIncludeDisabledDevices() {
		return includeDisabledDevices;
	}

	public DeviceCollection getDeviceCollection() {
		return deviceCollection;
	}

	public Set<Attribute> getAttributes() {
		return attributes;
	}
	
	public String toString() {
		String returnString = "[Days Previous: " + daysPrevious
				+ ", OnlyLatestEvent: " + onlyLatestEvent
				+ ", OnlyAbnormalEvents: " + onlyAbnormalEvents
				+ ", IncludeDisabledDevices: " + includeDisabledDevices;
		for(Attribute attribute : attributes) {
			returnString += attribute.getMessage() + ", ";
		}
		returnString += ", DeviceCollection: " + deviceCollection.getDescription() + "]";		
				
		return returnString;
	}
}
