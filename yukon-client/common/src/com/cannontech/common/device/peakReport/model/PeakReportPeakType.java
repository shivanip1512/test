package com.cannontech.common.device.peakReport.model;

import com.cannontech.common.i18n.DisplayableEnum;

public enum PeakReportPeakType implements DisplayableEnum {
    
    DAY, 
    HOUR, 
    INTERVAL;
    
    private String baseKey = "yukon.common.peakReportPeakType.";
    
    public String getDisplayName(){
        return name();
    }
    
    public String getReportTypeDisplayNameKey(){
        return baseKey + name() + ".description";
    }

	@Override
	public String getFormatKey() {
		return baseKey + name();
	}
}
