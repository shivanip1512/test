package com.cannontech.maintenance.task.service;

import org.joda.time.Instant;

public interface DrReconciliationService {
    /**
     * Method to start DR reconciliation.
     */
    boolean startDRReconciliation(Instant processEndTime);

}
