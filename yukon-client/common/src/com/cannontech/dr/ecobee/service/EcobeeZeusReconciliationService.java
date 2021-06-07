package com.cannontech.dr.ecobee.service;

import com.cannontech.dr.ecobee.EcobeeCommunicationException;

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
}