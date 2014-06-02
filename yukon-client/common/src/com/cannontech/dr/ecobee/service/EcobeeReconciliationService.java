package com.cannontech.dr.ecobee.service;

import java.util.List;

import com.cannontech.dr.ecobee.EcobeeCommunicationException;
import com.cannontech.dr.ecobee.model.EcobeeReconciliationReport;

/**
 * Creates and retrieves ecobee reconciliation reports, which describe discrepancies between Yukon's ecobee groups and 
 * thermostats, and the ecobee portal's hierarchy of management sets and thermostats. Also handles fixing those 
 * discrepancies by adding, moving, or removing objects from the ecobee portal.
 */
public interface EcobeeReconciliationService {
    
    /**
     * Runs a new report, comparing Yukon's ecobee setup to the ecobee portal's setup.
     * @return the ID of the new report.
     * @throws EcobeeCommunicationException if an error occurs attempting to read the ecobee hierarchy.
     */
    public int runReconciliationReport() throws EcobeeCommunicationException;
    
    /**
     * Retrieves the latest report. Returns null if no report has been run.
     */
    public EcobeeReconciliationReport findReconciliationReport();
    
    /**
     * Fixes the specified discrepancy by making changes to the ecobee portal's hierarchy.
     * @throws EcobeeCommunicationException if the specified reportId is outdated.
     */
    public List<String> fixDiscrepancy(int reportId, int errorId) throws EcobeeCommunicationException;
    
    /**
     * Fixes the all discrepancies in the specified report by making changes to the ecobee portal's hierarchy.
     * @throws EcobeeCommunicationException if the specified reportId is outdated.
     */
    public List<String> fixAllDiscrepancies(int reportId) throws EcobeeCommunicationException;
}
