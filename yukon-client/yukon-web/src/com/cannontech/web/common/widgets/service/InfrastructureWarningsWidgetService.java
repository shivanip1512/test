package com.cannontech.web.common.widgets.service;

import java.util.List;

import org.joda.time.Instant;

import com.cannontech.infrastructure.model.InfrastructureWarning;
import com.cannontech.infrastructure.model.InfrastructureWarningSummary;

public interface InfrastructureWarningsWidgetService {
    /**
     * If nextRunTime is true returns next time refresh can be attempted, otherwise return time the data was collected last.
     */
    Instant getRunTime(boolean nextRunTime);
    
    /**
     * Get a summary of the current warnings and devices, by device categories.
     */
    InfrastructureWarningSummary getWarningsSummary();
    
    /**
     * Retrieve the current infrastructure warnings.
     */
    public List<InfrastructureWarning> getWarnings();

    /**
     * Initiates recalculations of the infrastructure warnings.
     */
    void initiateRecalculation();

}
