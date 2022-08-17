package com.cannontech.dr.ecobee.dao;

import com.cannontech.dr.ecobee.model.EcobeeReconciliationReport;

/**
 * Reads and writes ecobee reconciliation reports, which describe discrepancies between Yukon's ecobee groups and 
 * thermostats, and the ecobee portal's hierarchy of management sets and thermostats.
 */
public interface EcobeeReconciliationReportDao {
    
    /**
     * Retrieves the most recent reconciliation report. Returns null if no report has been run.
     */
    public EcobeeReconciliationReport findReport();
    
    /**
     * Deletes the previous report and adds a new report to the database.
     */
    public int insertReport(EcobeeReconciliationReport report);
    
    /**
     * Delete the specified error from the report.
     */
    public boolean removeError(int reportId, int errorId);
}
