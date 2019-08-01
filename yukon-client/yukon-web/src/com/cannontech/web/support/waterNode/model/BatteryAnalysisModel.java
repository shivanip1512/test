package com.cannontech.web.support.waterNode.model;

import org.joda.time.Instant;

public class BatteryAnalysisModel {
    private Instant analysisEnd;
    private Instant lastCreatedReport;
    
    public Instant getAnalysisEnd() {
        return analysisEnd;
    }
    
    public void setAnalysisEnd(Instant analysisEnd) {
        this.analysisEnd= analysisEnd;
    }
    
    public Instant getLastCreatedReport() {
        return lastCreatedReport;
    }
    
    public void setLastCreatedReport(Instant lastCreatedReport) {
        this.lastCreatedReport = lastCreatedReport;
    }
}
