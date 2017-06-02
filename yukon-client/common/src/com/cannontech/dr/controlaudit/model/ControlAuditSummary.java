package com.cannontech.dr.controlaudit.model;

import org.joda.time.Instant;

public class ControlAuditSummary {

    private final String programName;
    private final Instant startTime;
    private final int numConfirmed;
    private final int numUnknowns;

    public ControlAuditSummary(String programName, Instant startTime, int numConfirmed, int numUnknowns) {
        this.programName = programName;
        this.startTime = startTime;
        this.numConfirmed = numConfirmed;
        this.numUnknowns = numUnknowns;
    }

    public int getNumUnknowns() {
        return numUnknowns;
    }

    public int getNumConfirmed() {
        return numConfirmed;
    }

    public String getProgramName() {
        return programName;
    }

    public Instant getStartTime() {
        return startTime;
    }

}
