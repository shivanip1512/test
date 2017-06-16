package com.cannontech.dr.recenteventparticipation.model;

import org.joda.time.Instant;

public class RecentEventParticipationStats extends RecentEventParticipationBase {

    private final int numConfirmed;
    private final int numUnknowns;
    private final RecentEventParticipationEventStats eventStats;

    public RecentEventParticipationStats(int controlEventId, String programName, String groupName, Instant startTime,
            int numConfirmed, int numUnknowns) {
        super(controlEventId, programName, groupName, startTime);
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
}
