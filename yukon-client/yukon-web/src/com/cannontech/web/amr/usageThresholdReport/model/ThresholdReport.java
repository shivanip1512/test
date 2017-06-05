package com.cannontech.web.amr.usageThresholdReport.model;

import java.util.List;

public class ThresholdReport {
    
    private int deviceCount;
    private List<ThresholdReportDetail> details;

    public ThresholdReport(int deviceCount, List<ThresholdReportDetail> details) {
        this.deviceCount = deviceCount;
        this.details = details;
    }

    public List<ThresholdReportDetail> getDetails() {
        return details;
    }

    public int getDeviceCount() {
        return deviceCount;
    }
}
