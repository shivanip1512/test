package com.cannontech.dr.recenteventparticipation.model;

import org.joda.time.Instant;

public class RecentEventParticipationSummary {

    private final String programName;
    private final Instant startTime;
    private final int numConfirmed;
    private final int numUnknowns;
    private final RecentEventParticipationEventStats eventStats;

    public RecentEventParticipationSummary(String programName, Instant startTime, int numConfirmed, int numUnknowns) {
        this.programName = programName;
        this.startTime = startTime;
        this.numConfirmed = numConfirmed;
        this.numUnknowns = numUnknowns;
        this.eventStats = new RecentEventParticipationEventStats(numConfirmed, numUnknowns);
    }

    public RecentEventParticipationEventStats getEventStats() {
        return eventStats;
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
