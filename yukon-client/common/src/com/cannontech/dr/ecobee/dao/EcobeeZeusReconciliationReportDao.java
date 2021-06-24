package com.cannontech.dr.ecobee.dao;

import com.cannontech.dr.ecobee.model.EcobeeZeusReconciliationReport;

/**
 * Reads and writes ecobee reconciliation reports, which describe discrepancies between Yukon's ecobee groups and 
 * thermostats, and the ecobee zeus portal's groups and thermostats.
 */
public interface EcobeeZeusReconciliationReportDao {
    
    /**
     * Retrieves the most recent reconciliation report. Returns null if no report has been run.
     */
    EcobeeZeusReconciliationReport findReport();
    
    /**
     * Deletes the previous report and adds a new report to the database.
     */
    int insertReport(EcobeeZeusReconciliationReport report);
    
    /**
     * Delete the specified error from the report.
     */
    boolean removeError(int reportId, int errorId);
    
    /*
     * Deleting rows from Reconciliation report and Reconciliation error tables.
     */
    void cleanUpReconciliationTables();
}
