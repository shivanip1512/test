package com.cannontech.maintenance.task.service;

import org.joda.time.Instant;

public interface DrReconciliationService {
    /**
     * Method to send appropriate message to LCR's.
     */
    boolean doDrReconcillation(Instant processEndTime);

}
