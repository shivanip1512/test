package com.cannontech.infrastructure.dao;

import java.util.Collection;
import java.util.List;

import com.cannontech.infrastructure.model.InfrastructureWarning;
import com.cannontech.infrastructure.model.InfrastructureWarningDeviceCategory;
import com.cannontech.infrastructure.model.InfrastructureWarningSummary;

/**
 * Dao for saving and retrieving infrastructure warnings.
 */
public interface InfrastructureWarningsDao {
       
    /**
     * The minimum amount of time that has to pass between recalculating warnings. This is used to determine if a
     * recalc request initiates a recalculation, and to determine how often to refresh cached data.
     */
    public static final int minimumMinutesBetweenCalculations = 5;
    
    /**
     * Replace the previously saved infrastructure warnings with new warnings.
     */
    public void insert(Collection<InfrastructureWarning> warnings);
    
    /**
     * Retrieve the current infrastructure warnings.
     */
    public List<InfrastructureWarning> getWarnings();
    
    /**
     * Retrieve the current infrastructure warnings for the specified categories of devices and whether to return only high severity warnings.
     */
    public List<InfrastructureWarning> getWarnings(Boolean highSeverityOnly, InfrastructureWarningDeviceCategory... categories);
    
    /**
     * Get a summary of the current warnings and devices, by device categories.
     */
    public InfrastructureWarningSummary getWarningsSummary();

    /**
     * Retrieve the current infrastructure warnings by deviceId.
     */
    List<InfrastructureWarning> getWarnings(int deviceId);
}

