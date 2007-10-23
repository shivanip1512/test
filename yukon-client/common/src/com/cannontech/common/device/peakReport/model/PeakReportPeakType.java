package com.cannontech.common.device.peakReport.model;

public enum PeakReportPeakType {
    
    DAY("Day","Peak Daily Usage"), 
    HOUR("Hour","Peak Hourly Usage"), 
    INTERVAL("Interval", "Peak Interval");
    
    private String displayName;
    private String reportTypeDisplayName;
    
    PeakReportPeakType(String displayName, String reportTypeDisplayName){
        
        this.displayName = displayName;
        this.reportTypeDisplayName = reportTypeDisplayName;
    }
    
    public String displayName(){
        return displayName;
    }
    
    public String reportTypeDisplayName(){
        return reportTypeDisplayName;
    }
}
