package com.cannontech.dr.ecobee.service;

import java.util.List;

import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.dr.ecobee.EcobeeCommunicationException;
import com.cannontech.dr.ecobee.model.EcobeeZeusReconciliationReport;
import com.cannontech.dr.ecobee.model.EcobeeZeusReconciliationResult;

/**
 * Creates and retrieves ecobee reconciliation reports, which describe discrepancies between Yukon's ecobee groups and 
 * thermostats, and the ecobee Zeus portal's groups and thermostats. Also handles fixing those 
 * discrepancies by adding, moving, or removing objects from the ecobee portal.
 */
public interface EcobeeZeusReconciliationService {

    /**
     * Runs a new report, comparing Yukon's ecobee setup to the ecobee zeus portal's setup.
     */
    int runReconciliationReport() throws EcobeeCommunicationException;
    
    /**
     * Retrieves the latest report. Returns null if no report has been run.
     */
    EcobeeZeusReconciliationReport findReconciliationReport();
    
    /**
     * Fixes the specified discrepancy by making changes to the ecobee zeus portal's hierarchy.
     * @throws IllegalArgumentException if the specified reportId is outdated, or errorId is invalid.
     */
    EcobeeZeusReconciliationResult fixDiscrepancy(int reportId, int errorId, LiteYukonUser liteYukonUser)
            throws IllegalArgumentException;
    
    /**
     * Fixes the all discrepancies in the specified report by making changes to the ecobee zeus portal's hierarchy.
     * @throws IllegalArgumentException if the specified reportId is outdated.
     */
    List<EcobeeZeusReconciliationResult> fixAllDiscrepancies(int reportId, LiteYukonUser liteYukonUser)
            throws IllegalArgumentException;
}
