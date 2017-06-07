package com.cannontech.dr.controlaudit.model;

import org.joda.time.Instant;

public class ControlAuditStats extends ControlAuditBase {

    private final int numConfirmed;
    private final int numUnknowns;
    private final ControlAuditEventStats eventStats;

    public ControlAuditStats(int controlEventId, String programName, String groupName, Instant startTime,
            int numConfirmed, int numUnknowns) {
        super(controlEventId, programName, groupName, startTime);
        this.numConfirmed = numConfirmed;
        this.numUnknowns = numUnknowns;
        this.eventStats = new ControlAuditEventStats(numConfirmed, numUnknowns);

    }

    public ControlAuditEventStats getEventStats() {
        return eventStats;
    }

    public int getNumUnknowns() {
        return numUnknowns;
    }

    public int getNumConfirmed() {
        return numConfirmed;
    }
}
