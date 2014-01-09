package com.cannontech.common.bulk.model;

public class AnalysisWithDeviceCount extends Analysis {
    int deviceCount = 0;
    
    public AnalysisWithDeviceCount(Analysis analysis, int deviceCount) {
        super(analysis);
        this.deviceCount = deviceCount;
    }
    
    public int getDeviceCount() {
        return deviceCount;
    }
}
