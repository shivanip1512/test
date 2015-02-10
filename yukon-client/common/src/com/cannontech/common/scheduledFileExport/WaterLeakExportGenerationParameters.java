package com.cannontech.common.scheduledFileExport;

import com.cannontech.common.bulk.collection.device.model.DeviceCollection;

public class WaterLeakExportGenerationParameters implements ExportFileGenerationParameters {
	private final DeviceCollection deviceCollection;
    private final int hoursPrevious;
    private final double threshold;
    private final boolean includeDisabledPaos;
	
    public WaterLeakExportGenerationParameters(DeviceCollection deviceCollection, int hoursPrevious, double threshold, boolean includeDisabledPaos) {
    	this.deviceCollection = deviceCollection;
    	this.hoursPrevious = hoursPrevious;
    	this.threshold = threshold;
    	this.includeDisabledPaos = includeDisabledPaos;
    }
    
    @Override
	public ScheduledExportType getExportType() {
		return ScheduledExportType.WATER_LEAK;
	}
    
	public DeviceCollection getDeviceCollection() {
		return deviceCollection;
	}

	public int getHoursPrevious() {
		return hoursPrevious;
	}

	public double getThreshold() {
		return threshold;
	}

	public boolean isIncludeDisabledPaos() {
		return includeDisabledPaos;
	}
	
	@Override
	public String toString() {
		return "[HoursPrevious: " + hoursPrevious 
				+ ", Threshold: " + threshold 
				+ ", IncludeDisabledPaos: " + includeDisabledPaos 
				+ ", DeviceCollection: " + deviceCollection.getDescription()
				+ "]";
	}
}
