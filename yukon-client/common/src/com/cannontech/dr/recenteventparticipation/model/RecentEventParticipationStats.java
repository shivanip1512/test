package com.cannontech.dr.recenteventparticipation.model;

import org.joda.time.Instant;

public class RecentEventParticipationStats extends RecentEventParticipationBase {

    private final int numConfirmed;
    private final int numUnknowns;
    private final int numFailed;
    private final int numConfirmedAfterRetry;
    private final RecentEventParticipationEventStats eventStats;

    public RecentEventParticipationStats(int controlEventId, String programName, String groupName, Instant startTime,
            int numConfirmed, int numUnknowns, String externalEventId, int numFailed, int numConfirmedAfterRetry) {
        super(controlEventId, programName, groupName, startTime, externalEventId);
        this.numConfirmed = numConfirmed;
        this.numUnknowns = numUnknowns;
        this.numFailed = numFailed;
        this.numConfirmedAfterRetry = numConfirmedAfterRetry;
        this.eventStats = new RecentEventParticipationEventStats(numConfirmed, numUnknowns, numFailed, numConfirmedAfterRetry);

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

    public int getNumFailed() {
        return numFailed;
    }

    public int getNumConfirmedAfterRetry() {
        return numConfirmedAfterRetry;
    }
}