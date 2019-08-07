package com.cannontech.web.support.waterNode.model;

import org.joda.time.Instant;

public class BatteryAnalysisModel {
    private Instant analysisEnd;
    private Instant lastCreatedReport;
    private Instant csvEndDate;
    
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

    public Instant getCsvEndDate() {
        return csvEndDate;
    }

    public void setCsvEndDate(Instant csvEndDate) {
        this.csvEndDate = csvEndDate;
    }
}
