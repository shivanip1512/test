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
     * Replace the previously saved infrastructure warnings with new warnings.
     */
    public void insert(Collection<InfrastructureWarning> warnings);
    
    /**
     * Retrieve the current infrastructure warnings.
     */
    public List<InfrastructureWarning> getWarnings();
    
    /**
     * Retrieve the current infrastructure warnings for the specified categories of devices.
     */
    public List<InfrastructureWarning> getWarnings(InfrastructureWarningDeviceCategory... categories);
    
    /**
     * Get a summary of the current warnings and devices, by device categories.
     */
    public InfrastructureWarningSummary getWarningsSummary();
}
