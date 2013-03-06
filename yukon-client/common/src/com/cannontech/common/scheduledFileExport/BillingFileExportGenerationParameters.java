package com.cannontech.common.scheduledFileExport;

import java.util.Collection;

import com.cannontech.common.device.groups.model.DeviceGroup;

public class BillingFileExportGenerationParameters implements ExportFileGenerationParameters {
	private final Collection<? extends DeviceGroup> deviceGroups;
	private final int formatId;
	private final int demandDaysPrevious;
	private final int energyDaysPrevious;
	private final boolean removeMultiplier;
	
	public BillingFileExportGenerationParameters(int formatId, Collection<? extends DeviceGroup> deviceGroups, int demandDaysPrevious, 
			int energyDaysPrevious, boolean removeMultiplier) {
		this.deviceGroups = deviceGroups;
		this.formatId = formatId;
		this.demandDaysPrevious = demandDaysPrevious;
		this.energyDaysPrevious = energyDaysPrevious;
		this.removeMultiplier = removeMultiplier;
	}
	
	@Override
	public ScheduledExportType getExportType() {
		return ScheduledExportType.BILLING;
	}

	public Collection<? extends DeviceGroup> getDeviceGroups() {
		return deviceGroups;
	}

	public int getFormatId() {
		return formatId;
	}

	public int getDemandDaysPrevious() {
		return demandDaysPrevious;
	}

	public int getEnergyDaysPrevious() {
		return energyDaysPrevious;
	}

	public boolean isRemoveMultiplier() {
		return removeMultiplier;
	}
	
	public String toString() {
		return "[FormatId: " + formatId
				+ ", Demand Days Previous: " + demandDaysPrevious
				+ ", Energy Days Previous: " + energyDaysPrevious
				+ ", Remove Multiplier: " + removeMultiplier
				+ ", Device Groups: " + deviceGroups.toString()
				+ "]";
	}
}
