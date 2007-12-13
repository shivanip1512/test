package com.cannontech.analysis.tablemodel;

public enum BulkImportResultstReportType {
    
    FAILURES("Failed Entries"), 
    PENDING_COMMS("Pending Communications"), 
    FAILED_COMMS("Failed Communications");
    
    private String reportDescription;
    
    BulkImportResultstReportType(String reportDescription){
        this.reportDescription = reportDescription;
    }

    public String getReportDecription() {
        return reportDescription;
    }
}