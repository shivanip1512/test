package com.cannontech.maintenance.task.service;

import java.util.Set;

import org.joda.time.Instant;

public interface DrReconciliationService {
    /**
     * Give a list of LCR which have conflicting addresses and messages have to be send for them.
     */
    Set<Integer> getLCRWithConflictingAddressing();

    /**
     * Method to send appropriate message to LCR's.
     */
    boolean doDrReconcillation(Instant processEndTime);

}
